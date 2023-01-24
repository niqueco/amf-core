package amf.core.internal.plugins.document.graph.parser

import amf.core.client.scala.model.document.SourceMap
import amf.core.client.scala.model.domain.extensions.{CustomDomainProperty, DomainExtension}
import amf.core.client.scala.model.domain._
import amf.core.internal.annotations.DomainExtensionAnnotation
import amf.core.internal.metamodel.Type.Iri
import amf.core.internal.metamodel.domain.DomainElementModel
import amf.core.internal.metamodel.domain.extensions.DomainExtensionModel
import amf.core.internal.metamodel.{Field, ModelDefaultBuilder, Type}
import amf.core.internal.parser.YMapOps
import amf.core.internal.parser.domain.{Annotations, FieldEntry}
import amf.core.internal.validation.CoreValidations.{UnableToParseDomainElement, UnableToParseNode}
import org.yaml.model._

abstract class CommonGraphParser(implicit ctx: GraphParserContext) extends GraphParserHelpers {

  protected def parse(map: YMap): Option[AmfObject]

  protected def parseAtTraversion(node: YNode, `type`: Type): Option[AmfElement]

  protected def traverseFields(
      map: YMap,
      fields: Seq[Field],
      instance: AmfObject,
      sources: SourceMap,
      nodes: Map[String, AmfElement]
  ): Unit = {
    fields.foreach(f => {
      val k = compactUriFromContext(f.value.iri()) // we are assuming compact uris, we shouldn't!
      map.key(k) match {
        case Some(entry) =>
          traverse(instance, f, value(f.`type`, entry.value), sources, k, nodes)
        case _ => // Ignore
      }
    })
  }

  private def traverse(
      instance: AmfObject,
      f: Field,
      node: YNode,
      sources: SourceMap,
      key: String,
      nodes: Map[String, AmfElement]
  ): AmfObject = {
    if (assertFieldTypeWithContext(f)(ctx)) {
      doTraverse(instance, f, node, sources, key, nodes)
    } else instance
  }

  private def doTraverse(
      instance: AmfObject,
      f: Field,
      node: YNode,
      sources: SourceMap,
      key: String,
      nodes: Map[String, AmfElement]
  ): AmfObject = {
    parseAtTraversion(node, f.`type`).foreach(r => instance.setWithoutId(f, r, annotations(nodes, sources, key)))
    instance
  }

  protected def findType(typeIris: Seq[String], id: String, map: YMap): Option[ModelDefaultBuilder] = {
    typeIris.find(findType(_).isDefined) match {
      case Some(t) => findType(t)
      case None =>
        ctx.eh
          .violation(UnableToParseNode, id, s"Error parsing JSON-LD node, unknown @types $typeIris", map.location)
        None
    }
  }

  protected def applyScalarDomainProperties(instance: DomainElement, scalars: Seq[DomainExtension]): Unit = {
    scalars.foreach { e =>
      instance.fields
        .fieldsMeta()
        .find(f => e.element.is(f.value.iri()))
        .foreach(f => {
          instance.fields.entry(f).foreach { case FieldEntry(_, value) =>
            value.value.annotations += DomainExtensionAnnotation(e)
          }
        })
    }
  }

  protected def yNodeSeq(
      node: YNode,
      a: Type.Array
  ): Option[AmfArray] = {
    val rawItems = node.as[Seq[YNode]]
    val values = rawItems.flatMap { ri =>
      parseAtTraversion(value(a.element, ri), a.element)
    }
    Some(AmfArray(values))
  }

  protected def parseCustomProperties(map: YMap, instance: DomainElement, nodes: Map[String, AmfElement]): Unit = {
    // See ADR adrs/0006-custom-domain-properties-json-ld-rendering.md last consequence item
    val extensions: Seq[DomainExtension] = for {
      uri       <- customDomainPropertiesFor(map)
      entry     <- asSeq(map.key(transformIdFromContext(uri)))
      extension <- parseCustomDomainPropertyEntry(uri, entry, nodes)
    } yield {
      extension
    }
    if (extensions.nonEmpty) {
      extensions.partition(_.isScalarExtension) match {
        case (scalars, objects) =>
          instance.withCustomDomainProperties(objects)
          applyScalarDomainProperties(instance, scalars)
      }
    }
  }

  private def customDomainPropertiesFor(map: YMap): Seq[String] = {
    val fieldIri   = DomainElementModel.CustomDomainProperties.value.iri()
    val compactIri = compactUriFromContext(fieldIri)

    map.key(compactIri) match {
      case Some(entry) =>
        for {
          valueNode <- entry.value.as[Seq[YNode]]
        } yield {
          value(Iri, valueNode).as[YScalar].text
        }
      case _ =>
        Nil
    }
  }

  private def parseCustomDomainPropertyEntry(
      uri: String,
      entry: YMapEntry,
      nodes: Map[String, AmfElement]
  ): Seq[DomainExtension] = {
    entry.value.tagType match {
      case YType.Map =>
        Seq(parseSingleDomainExtension(entry.value.as[YMap], uri, nodes))
      case YType.Seq =>
        val values = entry.value.as[YSequence]
        values.nodes.map { value =>
          parseSingleDomainExtension(value.as[YMap], uri, nodes)
        }
      case _ =>
        ctx.eh
          .violation(UnableToParseDomainElement, uri, s"Cannot parse domain extensions for '$uri'", entry.location)
        Nil
    }
  }

  private def parseSingleDomainExtension(
      map: YMap,
      uri: String,
      nodes: Map[String, AmfElement]
  ): DomainExtension = {
    val extension = DomainExtension()
    contentOfNode(map) match {
      case Some(obj) =>
        parseScalarProperty(obj, DomainExtensionModel.Name)
          .map(s => extension.set(DomainExtensionModel.Name, s))
        parseScalarProperty(obj, DomainExtensionModel.Element)
          .map(extension.withElement)

        val definition = CustomDomainProperty()
        definition.id = transformIdFromContext(uri)
        extension.withDefinedBy(definition)

        parse(obj).collect({ case d: DataNode => d }).foreach { pn =>
          extension.withId(pn.id)
          extension.withExtension(pn)
        }

        val sources = retrieveSources(obj)
        extension.annotations ++= annotations(nodes, sources, extension.id)
      case None =>
        val nodeId = s"${retrieveId(map, ctx)}"
        ctx.eh.violation(
          UnableToParseDomainElement,
          nodeId,
          s"Cannot find node definition for node '$nodeId'",
          map.location
        )
    }
    extension
  }

  private def parseScalarProperty(definition: YMap, field: Field): Option[String] =
    definition
      .key(compactUriFromContext(field.value.iri()))
      .map(entry => value(field.`type`, entry.value).as[YScalar].text)

  protected def annotations(nodes: Map[String, AmfElement], sources: SourceMap, key: String): Annotations =
    ctx.config.serializableAnnotationsFacade.retrieveAnnotation(nodes, sources, key)

  private def findType(typeString: String): Option[ModelDefaultBuilder] = {
    ctx.config.registryContext.findType(expandUriFromContext(typeString))
  }
}
