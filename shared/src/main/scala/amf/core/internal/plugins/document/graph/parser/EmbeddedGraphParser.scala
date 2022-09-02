package amf.core.internal.plugins.document.graph.parser

import amf.core.client.scala.model.document._
import amf.core.client.scala.model.domain._
import amf.core.client.scala.parse.document.SyamlParsedDocument
import amf.core.client.scala.vocabulary.Namespace
import amf.core.internal.metamodel.Type.{Array, Bool, Iri, LiteralUri, RegExp, SortedArray, Str}
import amf.core.internal.metamodel._
import amf.core.internal.metamodel.document.BaseUnitModel.Location
import amf.core.internal.metamodel.domain._
import amf.core.internal.parser._
import amf.core.internal.plugins.document.graph.JsonLdKeywords
import amf.core.internal.validation.CoreValidations.{UnableToParseDocument, UnableToParseNode}
import org.yaml.convert.YRead.SeqNodeYRead
import org.yaml.model._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/** AMF Graph parser
  */
class EmbeddedGraphParser(private val aliases: Map[String, String])(implicit val ctx: GraphParserContext) extends GraphContextHelper
     {

  def canParse(document: SyamlParsedDocument): Boolean = EmbeddedGraphParser.canParse(document)

  def parse(document: YDocument, location: String): BaseUnit = {
    val parser = Parser(mutable.Map())
    parser.parse(document, location)
  }

  case class Parser(override val nodes: mutable.Map[String, AmfElement]) extends GraphParserHelpers(nodes){

    def parse(document: YDocument, location: String): BaseUnit = {
      val parsedOption = for {
        seq  <- document.node.toOption[Seq[YMap]]
        head <- seq.headOption
        parsed <- {
          head.key(JsonLdKeywords.Context, e => JsonLdGraphContextParser(e.value, ctx).parse())
          aliases.foreach { case (term, iri) =>
            ctx.graphContext.withTerm(term, iri)
          }
          parse(head)
        }
      } yield {
        parsed
      }

      parsedOption match {
        case Some(unit: BaseUnit) => unit.set(Location, location)
        case _ =>
          ctx.eh.violation(UnableToParseDocument, location, s"Unable to parse $document", document.location)
          Document()
      }
    }

    private def retrieveType(id: String, map: YMap): Option[ModelDefaultBuilder] = {
      val stringTypes = ts(map, id)
      stringTypes.find(findType(_).isDefined) match {
        case Some(t) => findType(t)
        case None =>
          ctx.eh
            .violation(UnableToParseNode, id, s"Error parsing JSON-LD node, unknown @types $stringTypes", map.location)
          None
      }
    }

    private def parseList(listElement: Type, node: YMap): Seq[AmfElement] = {
      val buffer = ListBuffer[YNode]()
      node.entries.sortBy(_.key.as[String]).foreach { entry =>
        if (entry.key.as[String].startsWith(compactUriFromContext((Namespace.Rdfs + "_").iri()))) {
          buffer += entry.value.as[Seq[YNode]].head
        }
      }
      buffer.flatMap { n =>
        listElement match {
          case _: Obj   => parse(n.as[YMap])
          case Type.Any => Some(typedValue(n, ctx.graphContext))
          case _ =>
            try { Some(str(value(listElement, n))) }
            catch {
              case _: Exception => None
            }
        }
      }
    }

    override protected def parse(map: YMap): Option[AmfObject] = {
      retrieveId(map, ctx)
        .flatMap(value => retrieveType(value, map).map(value2 => (value, value2)))
        .flatMap {
          case (id, model) =>
            val sources               = retrieveSources(map)
            val transformedId: String = transformIdFromContext(id)

            val instance: AmfObject = buildType(model, annotations(nodes, sources, transformedId))
            instance.withId(transformedId)

            // workaround for lazy values in shape
            val modelFields = model match {
              case shapeModel: ShapeModel =>
                shapeModel.fields ++ Seq(
                    ShapeModel.CustomShapePropertyDefinitions,
                    ShapeModel.CustomShapeProperties
                )
              case _ => model.fields
            }

            modelFields.foreach(f => {
              val k = compactUriFromContext(f.value.iri())
              map.key(k) match {
                case Some(entry) =>
                  traverse(instance, f, value(f.`type`, entry.value), sources, k)
                case _ =>
              }
            })

            checkLinkables(instance)

            // parsing custom extensions
            instance match {
              case l: DomainElement with Linkable =>
                parseLinkableProperties(map, l)
              case obj: ObjectNode =>
                parseObjectNodeProperties(obj, map, modelFields)

              case _ => // ignore
            }

            instance match {
              case ex: ExternalDomainElement
                  if unresolvedExtReferencesMap
                    .contains(ex.id) => // check if other node requested this external reference
                unresolvedExtReferencesMap.get(ex.id).foreach { element =>
                  ex.raw
                    .option()
                    .foreach(element.set(ExternalSourceElementModel.Raw, _))
                }
              case _ => // ignore
            }

            instance match {
              case elm: DomainElement => parseCustomProperties(map, elm)
              case _                  => // ignore
            }

            nodes(transformedId) = instance
            Some(instance)
          case _ => None
        }
    }

    private def parseLinkableProperties(map: YMap, instance: DomainElement with Linkable): Unit = {
      val targetIdFieldIri = LinkableElementModel.TargetId.value.iri()
      map
        .key(compactUriFromContext(targetIdFieldIri))
        .flatMap(entry => {
          retrieveId(entry.value.as[Seq[YMap]].head, ctx)
        })
        .foreach { targetId =>
          val transformedId = transformIdFromContext(targetId)
          setLinkTarget(instance, transformedId)
        }

      map
        .key(compactUriFromContext(LinkableElementModel.Label.value.iri()))
        .flatMap(entry => {
          entry.value
            .toOption[Seq[YNode]]
            .flatMap(nodes => nodes.head.toOption[YMap])
            .flatMap(map => map.key(JsonLdKeywords.Value))
            .flatMap(_.value.toOption[YScalar].map(_.text))
        })
        .foreach(s => instance.withLinkLabel(s))
    }

    override protected def mapValueFrom(entry: YMapEntry): YMap = entry.value.as[YMap]

    private def parseObjectNodeProperties(obj: ObjectNode, map: YMap, fields: List[Field]): Unit = {
      map.entries.foreach { entry =>
        val uri = expandUriFromContext(entry.key.as[String])
        val v   = entry.value
        if (
            uri != JsonLdKeywords.Type && uri != JsonLdKeywords.Id && uri != DomainElementModel.Sources.value
              .iri() && uri != "smaps" &&
            uri != (Namespace.Core + "extensionName").iri() && !fields
              .exists(_.value.iri() == uri)
        ) { // we do this to prevent parsing name of annotations
          v.as[Seq[YMap]]
            .headOption
            .flatMap(parse)
            .collect({ case d: amf.core.client.scala.model.domain.DataNode => obj.addProperty(uri, d) })
        }
      }
    }

    private def traverse(instance: AmfObject, f: Field, node: YNode, sources: SourceMap, key: String) = {
      if (assertFieldTypeWithContext(f)(ctx)) {
        doTraverse(instance, f, node, sources, key)
      } else instance
    }

    private def doTraverse(instance: AmfObject, f: Field, node: YNode, sources: SourceMap, key: String) = {
      f.`type` match {
        case _: Obj =>
          parse(node.as[YMap]).foreach(n => instance.setWithoutId(f, n, annotations(nodes, sources, key)))
          instance
        case Iri =>
          instance.setWithoutId(f, iri(node), annotations(nodes, sources, key))
        case Str | RegExp | LiteralUri =>
          instance.setWithoutId(f, str(node), annotations(nodes, sources, key))
        case Bool =>
          instance.setWithoutId(f, bool(node), annotations(nodes, sources, key))
        case Type.Int =>
          instance.setWithoutId(f, int(node), annotations(nodes, sources, key))
        case Type.Float =>
          instance.setWithoutId(f, double(node), annotations(nodes, sources, key))
        case Type.Double =>
          instance.setWithoutId(f, double(node), annotations(nodes, sources, key))
        case Type.Long =>
          instance.setWithoutId(f, long(node), annotations(nodes, sources, key))
        case Type.DateTime =>
          instance.setWithoutId(f, date(node), annotations(nodes, sources, key))
        case Type.Date =>
          instance.setWithoutId(f, date(node), annotations(nodes, sources, key))
        case Type.Any =>
          instance.setWithoutId(f, any(node), annotations(nodes, sources, key))
        case l: SortedArray =>
          instance.setArrayWithoutId(f, parseList(l.element, node.as[YMap]), annotations(nodes, sources, key))
        case a: Array =>
          val items = node.as[Seq[YNode]]
          val values: Seq[AmfElement] = a.element match {
            case _: Obj    => items.flatMap(n => parse(n.as[YMap]))
            case Str => items.map(n => str(value(a.element, n)))
            case Iri => items.map(n => iri(value(a.element, n)))
          }
          instance.setArrayWithoutId(f, values, annotations(nodes, sources, key))
      }
    }
  }

  private def findType(typeString: String): Option[ModelDefaultBuilder] = {
    ctx.config.registryContext.findType(expandUriFromContext(typeString))
  }
}

object EmbeddedGraphParser {

  def apply(config: ParseConfiguration, aliases: Map[String, String]): EmbeddedGraphParser =
    new EmbeddedGraphParser(aliases)(new GraphParserContext(config = config))

  def canParse(document: SyamlParsedDocument): Boolean = {
    val maybeMaps = document.document.node.toOption[Seq[YMap]]
    val maybeMap  = maybeMaps.flatMap(s => s.headOption)
    maybeMap match {
      case Some(m: YMap) =>
        val toDocumentNamespace: String => String = a => (Namespace.Document + a).iri()
        val keys                                  = Seq("encodes", "declares", "references").map(toDocumentNamespace)
        val types = Seq("Document", "Fragment", "Module", "Unit").map(toDocumentNamespace)

        val acceptedKeys  = keys ++ keys.map(Namespace.defaultAliases.compact)
        val acceptedTypes = types ++ types.map(Namespace.defaultAliases.compact)
        acceptedKeys.exists(m.key(_).isDefined) ||
        m.key(JsonLdKeywords.Type).exists { typesEntry =>
          val retrievedTypes = typesEntry.value.asOption[YSequence].map(stringNodesFrom)
          retrievedTypes.exists(acceptedTypes.intersect(_).nonEmpty)
        }
      case _ => false
    }
  }

  private def stringNodesFrom(seq: YSequence): IndexedSeq[Any] =
    seq.nodes.flatMap(node => node.asOption[YScalar]).map(_.value)
}
