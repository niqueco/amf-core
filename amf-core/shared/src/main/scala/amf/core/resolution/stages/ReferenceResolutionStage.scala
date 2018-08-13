package amf.core.resolution.stages

import amf.core.annotations.{DeclaredElement, ResolvedInheritance, ResolvedLinkAnnotation}
import amf.core.metamodel.document.DocumentModel
import amf.core.metamodel.domain.ExternalSourceElementModel
import amf.core.model.document.{BaseUnit, Document, EncodesModel}
import amf.core.model.domain._
import amf.core.parser.ErrorHandler
import org.mulesoft.common.core.Strings

import scala.collection.mutable

/**
  * Class that holds a resolved dynamic link node, with the original link information
  * @param source the link node that has been resolved
  * @param resolved the piece of domain linked through the resolved link node
  */
class ResolvedLinkNode(val source: LinkNode, val resolved: DomainElement)
// resolved so alias -> value
    extends LinkNode(source.value, source.value, source.fields, source.annotations) {
  linkedDomainElement = Some(resolved)

  override def cloneNode(): ResolvedLinkNode = new ResolvedLinkNode(source, resolved)
}

/**
  * Class to store the mapping of named assigned to the linked entity when resolved.
  * We cannot just overwrite name because that would be overwritten in every single place
  * where the entity has been included
  * @param vals map of names and named entities
  */
case class ResolvedNamedEntity(vals: mutable.HashMap[String, Seq[NamedDomainElement]] = mutable.HashMap())
    extends Annotation

class ModelReferenceResolver(model: BaseUnit) {

  def findFragment(url: String): Option[DomainElement] = {
    model match {
      case encodes: EncodesModel if model.location().exists(_.equals(url)) => Some(encodes.encodes)
      case _ if model.location().exists(_.equals(url))                     => None
      case _ =>
        var remaining                     = model.references.map(new ModelReferenceResolver(_))
        var result: Option[DomainElement] = None
        while (remaining.nonEmpty) {
          remaining.head.findFragment(url) match {
            case res: Some[DomainElement] =>
              result = res
              remaining = Nil
            case _ => remaining = remaining.tail
          }
        }
        result
    }
  }
}

/**
  * Resolves the local and remote references found in the model.
  */
class ReferenceResolutionStage(
    keepEditingInfo: Boolean,
    idsTraversionCheck: IdsTraversionCheck = IdsTraversionCheck(),
    cache: mutable.Map[String, DomainElement] = mutable.Map())(override implicit val errorHandler: ErrorHandler)
    extends ResolutionStage() {

  var mutuallyRecursive: Seq[String]                = Nil
  var model: Option[BaseUnit]                       = None
  var modelResolver: Option[ModelReferenceResolver] = None

  override def resolve[T <: BaseUnit](model: T): T = {
    this.model = Some(model)
    this.modelResolver = Some(new ModelReferenceResolver(model))
    model.transform(findLinkPredicates, transform).asInstanceOf[T]
  }

  def resolveDomainElement[T <: DomainElement](element: T): T = {
    val doc = Document().withId("http://resolutionstage.com/test#")
    if (element.id != null) {
      doc.fields.setWithoutId(DocumentModel.Encodes, element)
    } else {
      doc.withEncodes(element)
    }
    resolve(doc).encodes.asInstanceOf[T]
  }

  def resolveDomainElementSet[T <: DomainElement](elements: Seq[T]): Seq[DomainElement] = {
    val doc = Document().withId("http://resolutionstage.com/test#")

    doc.withDeclares(elements)
    resolve(doc).declares
  }

  // Internal request that checks for mutually recursive types
  protected def recursiveResolveInvocation(model: BaseUnit, modelResolver: Option[ModelReferenceResolver]): BaseUnit = {
    this.model = Some(model)
    this.modelResolver = Some(modelResolver.getOrElse(new ModelReferenceResolver(model)))
    model.transform(findLinkPredicates, transform)
  }

  def findLinkPredicates(element: DomainElement): Boolean = {
    val validLink = element match {
      case l: Linkable => l.isLink
      // link in a data node (trait or resource type) see DataNodeParser for details
      case l: LinkNode => true

      case _ => false
    }
    val externalSource = element match {
      case ex: ExternalSourceElement if !keepEditingInfo && ex.fields.exists(ExternalSourceElementModel.ReferenceId) =>
        true
      case _ => false
    }

    validLink || externalSource
  }

  def resolveDynamicLink(l: LinkNode): Option[DomainElement] = {
    l match {
      case r: ResolvedLinkNode => Some(r)
      case _ =>
        modelResolver.get.findFragment(l.value) match {
          case Some(elem) =>
            val resolved = new ResolvedLinkNode(l, elem).withId(l.id)
            if (keepEditingInfo) resolved.annotations += ResolvedLinkAnnotation(l.id)
            resolved.annotations += ResolvedInheritance()
            if (elem.annotations.contains(classOf[DeclaredElement])) resolved.annotations += DeclaredElement()
            Some(resolved)
          case None if l.linkedDomainElement.isDefined =>
            val resolved = new ResolvedLinkNode(l, l.linkedDomainElement.get).withId(l.id)
            if (keepEditingInfo) resolved.annotations += ResolvedLinkAnnotation(l.id)
            if (l.annotations.contains(classOf[DeclaredElement])) resolved.annotations += DeclaredElement()
            resolved.annotations += ResolvedInheritance()
            Some(resolved)
          case _ =>
            Some(l)
        }
    }
  }

  // Customisation of the resolution transformation for different domains
  protected def customDomainElementTransformation(d: DomainElement, source: Linkable): DomainElement = d

  def transform(element: DomainElement, isCycle: Boolean): Option[DomainElement] =
    cache.get(element.id).orElse(resolveAndCache(element, isCycle))

  private def resolveAndCache(element: DomainElement, isCycle: Boolean): Option[DomainElement] = {

    val resolved = element match {
      // link not traversed, cache it and traverse it
      case l: Linkable if l.linkTarget.isDefined && !isCycle =>
        val resolved = resolveReferenced(l.linkTarget.get)
        resolved match {
          case linkable: Linkable if l.supportsRecursion.option().getOrElse(false) =>
            linkable.withSupportsRecursion(true)
          case _ => // ignore
        }
        if (keepEditingInfo) resolved.annotations += ResolvedLinkAnnotation(l.id)
        resolved.annotations += ResolvedInheritance()
        if (element.annotations.contains(classOf[DeclaredElement])) resolved.annotations += DeclaredElement()
        Some(customDomainElementTransformation(withName(resolved, l), l))

      case l: Linkable if l.linkTarget.isDefined && isCycle =>
        Some(
          RecursiveShape(l)
            .withSupportsRecursion(l.supportsRecursion.option().getOrElse(false))) // todo add error here?

      // link traversed, return the link
      case l: Linkable if l.linkTarget.isDefined => Some(l)

      // Link inside a dynamic part of the graph, typically in a trait/resource node
      case l: LinkNode => resolveDynamicLink(l)

      // no link
      case ex: ExternalSourceElement =>
        ex.fields.removeField(ExternalSourceElementModel.ReferenceId)
        Some(resolveExternalFields(ex))
      case other => Some(other)

    }
    resolved.foreach { r =>
      cache.put(element.id, r)
    }

    resolved
  }
  private def resolveExternalFields(element: DomainElement): DomainElement = {

    element.fields.foreach {
      case (f, v) =>
        val newValue = v.value match {
          case a: AmfArray if a.values.headOption.exists(_.isInstanceOf[DomainElement]) =>
            AmfArray(a.values.map(v => resolveReferenced(v.asInstanceOf[DomainElement])))
          case d: DomainElement => resolveReferenced(d)
          case other            => other
        }
        element.fields.setWithoutId(f, newValue, v.annotations)
    }

    element
  }

  def withName(resolved: DomainElement, source: DomainElement): DomainElement = {
    resolved match {
      case r: NamedDomainElement
          if r.name.value().notNull.isEmpty || r.name.value() == "schema" || r.name
            .value() == "type" => // these are default names
        source match {
          case s: NamedDomainElement if s.name.value().notNull.nonEmpty => r.withName(s.name.value())
          case _                                                        =>
        }
      case _ =>
    }

    // let's annotate the resolved name
    source match {

      case s: NamedDomainElement if s.name.nonEmpty =>
        val resolvedNamesPresent          = resolved.annotations.find(classOf[ResolvedNamedEntity])
        val resolvedNamedEntityAnnotation = resolvedNamesPresent.getOrElse(ResolvedNamedEntity())
        val referenced                    = resolvedNamedEntityAnnotation.vals.getOrElse(s.name.value(), Nil)
        resolvedNamedEntityAnnotation.vals.put(s.name.value(), referenced ++ Seq(s))
        if (resolvedNamesPresent.isEmpty)
          resolved.annotations += resolvedNamedEntityAnnotation

      case s: Linkable if s.isInstanceOf[NamedDomainElement] && s.linkLabel.option().isDefined =>
        val resolvedNamesPresent          = resolved.annotations.find(classOf[ResolvedNamedEntity])
        val resolvedNamedEntityAnnotation = resolvedNamesPresent.getOrElse(ResolvedNamedEntity())
        val referenced                    = resolvedNamedEntityAnnotation.vals.getOrElse(s.linkLabel.value(), Nil)
        resolvedNamedEntityAnnotation.vals
          .put(s.linkLabel.value(), referenced ++ Seq(s.asInstanceOf[NamedDomainElement]))
        if (resolvedNamesPresent.isEmpty)
          resolved.annotations += resolvedNamedEntityAnnotation

      case _ => // ignore
    }

    resolved
  }

  def resolveReferenced(element: DomainElement): DomainElement = {
    cache.get(element.id) match {
      case Some(e)                                   => e
      case _ if idsTraversionCheck.hasId(element.id) => element
      case _ =>
        val nested = Document()
        nested.fields.setWithoutId(DocumentModel.Encodes, element)
        idsTraversionCheck + element.id
        val resolved = idsTraversionCheck.runPushed[DomainElement]((itc: IdsTraversionCheck) => {
          val result = new ReferenceResolutionStage(keepEditingInfo, idsTraversionCheck, cache)
            .recursiveResolveInvocation(nested, modelResolver)
          result.asInstanceOf[Document].encodes
        })
        cache.put(element.id, resolved)
        resolved
    }
  }

}