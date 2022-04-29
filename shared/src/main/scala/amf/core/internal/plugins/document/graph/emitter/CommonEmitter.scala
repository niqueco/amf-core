package amf.core.internal.plugins.document.graph.emitter

import amf.core.client.scala.config.RenderOptions
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.annotations.{Declares, References}
import amf.core.internal.metamodel.domain.ExternalSourceElementModel
import amf.core.internal.metamodel.{Field, Obj, Type}
import amf.core.client.scala.model.domain.{AmfArray, AmfElement, AmfObject, ExternalSourceElement}
import amf.core.internal.metamodel.document.BaseUnitModel
import amf.core.internal.parser.domain.FieldEntry
import amf.core.internal.parser.domain.{Annotations, FieldEntry}
import amf.core.internal.plugins.document.graph.MetaModelHelper.fieldsFrom
import amf.core.internal.plugins.document.graph.{JsonLdKeywords, MetaModelHelper}
import org.yaml.builder.DocBuilder.{Entry, Part}

trait ApplicableMetaFieldRenderProvider {
  def fieldsFor(element: AmfObject, renderOptions: RenderOptions): Seq[Field] =
    filterUnwantedElements(element, fieldsFrom(element.meta), renderOptions)

  private def filterUnwantedElements(element: AmfObject, fields: Seq[Field], options: RenderOptions) = {
    element match {
      case e: ExternalSourceElement if e.isLinkToSource && !options.rawFieldEmission =>
        fields.filter(f => f != ExternalSourceElementModel.Raw)
      case _: BaseUnit if !options.sourceInformation => fields.filter(f => f != BaseUnitModel.SourceInformation)
      case _                                         => fields
    }
  }

}

object InstanceMetaApplicableFieldRenderProvider extends ApplicableMetaFieldRenderProvider {
  override def fieldsFor(element: AmfObject, renderOptions: RenderOptions): Seq[Field] =
    super.fieldsFor(element, renderOptions)
}

object SemanticExtensionAwareMetaFieldRenderProvider {
  def apply(
      extensions: Map[String, Map[String, Type]],
      options: RenderOptions
  ): SemanticExtensionAwareFieldRenderProvision = {
    new SemanticExtensionAwareFieldRenderProvision(createDomainToExtensionMap(extensions), options)
  }

  private def createDomainToExtensionMap(extensionModels: Map[String, Map[String, Type]]): Map[String, Set[String]] =
    extensionModels.map { case (domain: String, extensionToType: Map[String, Type]) =>
      domain -> extensionToType.keySet
    }
}

class SemanticExtensionAwareFieldRenderProvision(
    domainExtensions: Map[String, Set[String]],
    renderOptions: RenderOptions
) extends ApplicableMetaFieldRenderProvider {
  override def fieldsFor(element: AmfObject, renderOptions: RenderOptions): Seq[Field] = {
    val fields = super.fieldsFor(element, renderOptions)
    val obj    = element.meta

    fields ++ getExtensionFields(element, obj, extensionsFor(obj, domainExtensions))
  }

  private def extensionsFor(obj: Obj, domainExtensions: Map[String, Set[String]]) = {
    obj.`type`
      .flatMap(valueType => domainExtensions.get(valueType.iri()))
      .foldLeft(Set[String]()) { (acc, curr) =>
        acc ++ curr
      }
  }

  private def getExtensionFields(element: AmfObject, obj: Obj, extensionIris: Set[String]): Seq[Field] = {
    val fieldsNotInObj = diffByIri(element.fields.fieldsMeta(), obj.fields)
    fieldsNotInObj.filter(x => extensionIris.contains(x.value.iri()))
  }

  // TODO: had to do this because when implementing hashCode in the Field case class, a lot of other entries popped up in JSON-LD
  private def diffByIri(fields: List[Field], otherFields: List[Field]): List[Field] = {
    val similar = (a: Field, b: Field) => a.equals(b)
    fields.filterNot { a =>
      otherFields.exists(b => similar(a, b))
    }
  }
}

trait CommonEmitter {

  def extractDeclarationsAndReferencesToContext(
      declaresEntry: Option[FieldEntry],
      referencesEntry: Option[FieldEntry],
      annotations: Annotations
  )(implicit ctx: GraphEmitterContext): ctx.type = {
    val declaredElements: Iterable[AmfElement] =
      declaresEntry.map(_.value.value.asInstanceOf[AmfArray].values).getOrElse(Nil)
    val referencedElements: Iterable[AmfElement] =
      referencesEntry.map(_.value.value.asInstanceOf[AmfArray].values).getOrElse(Nil)

    val declaredIds   = annotations.find(classOf[Declares]).map(_.declares).getOrElse(Nil)
    val referencedIds = annotations.find(classOf[References]).map(_.references).getOrElse(Nil)
    ctx.registerDeclaredAndReferencedFromAnnotations(declaredIds ++ referencedIds)

    ctx ++ declaredElements
    ctx.addReferences(referencedElements)
  }

  def sourceMapIdFor(id: String): String = {
    if (id.endsWith("/")) {
      id + "source-map"
    } else if (id.contains("#") || id.startsWith("null")) {
      id + "/source-map"
    } else {
      id + "#/source-map"
    }
  }

  def getTypesAsIris(obj: Obj): List[String] = obj.`type`.map(_.iri())

  def createTypeNode[T](b: Entry[T], types: List[String])(implicit ctx: GraphEmitterContext): Unit = {
    b.entry(
        JsonLdKeywords.Type,
        _.list { b =>
          types.distinct.foreach(t => raw(b, ctx.emitIri(t)))
        }
    )
  }

  def createTypeNode[T](b: Entry[T], obj: Obj)(implicit ctx: GraphEmitterContext): Unit =
    createTypeNode(b, getTypesAsIris(obj))

  def raw[T](b: Part[T], content: String): Unit = b += content
}
