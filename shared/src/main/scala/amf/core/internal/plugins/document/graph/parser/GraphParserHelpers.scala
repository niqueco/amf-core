package amf.core.internal.plugins.document.graph.parser

import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.DataType
import amf.core.client.scala.model.document.SourceMap
import amf.core.client.scala.model.domain.AmfScalar
import amf.core.client.scala.parse.document.ParserContext
import amf.core.client.scala.vocabulary.Namespace.SourceMaps
import amf.core.client.scala.vocabulary._
import amf.core.internal.metamodel.Type._
import amf.core.internal.metamodel.document.SourceMapModel
import amf.core.internal.metamodel.domain.DomainElementModel
import amf.core.internal.metamodel.{Field, Obj, Type}
import amf.core.internal.parser._
import amf.core.internal.plugins.document.graph.JsonLdKeywords
import amf.core.internal.plugins.document.graph.context.{
  ExpandedTermDefinition,
  GraphContext,
  GraphContextOperations,
  TermDefinition
}
import amf.core.internal.validation.CoreValidations.{MissingIdInNode, MissingTypeInNode}
import org.mulesoft.common.time.SimpleDateTime
import org.yaml.convert.YRead.SeqNodeYRead
import org.yaml.model._

import scala.collection.immutable

trait GraphParserHelpers extends GraphContextHelper {
  protected def double(node: YNode)(implicit errorHandler: IllegalTypeHandler): AmfScalar = {
    val value = node.tagType match {
      case YType.Map =>
        node.as[YMap].entries.find(_.key.as[String] == JsonLdKeywords.Value) match {
          case Some(entry) => entry.value.as[YScalar].text.toDouble
          case _           => node.as[YScalar].text.toDouble
        }
      case _ => node.as[YScalar].text.toDouble
    }
    AmfScalar(value)
  }

  protected def str(node: YNode)(implicit errorHandler: IllegalTypeHandler): AmfScalar = AmfScalar(stringValue(node))
  protected def typedValue(node: YNode, context: GraphContext)(implicit errorHandler: IllegalTypeHandler): AmfScalar = {
    val map          = node.as[YMap]
    val expandedIri  = map.key(JsonLdKeywords.Type).map(_.value.as[String]).map(expand(_)(context))
    val detectedType = expandedIri.getOrElse(DataType.String)
    detectedType match {
      case DataType.Boolean => bool(node)
      case DataType.Integer => int(node)
      case DataType.Long    => long(node)
      case DataType.Double  => double(node)
      case DataType.Float   => double(node)
      case _                => str(node)
    }
  }

  private def stringValue(node: YNode)(implicit errorHandler: IllegalTypeHandler): String =
    node.tagType match {
      case YType.Map =>
        node.as[YMap].entries.find(_.key.as[String] == JsonLdKeywords.Value) match {
          case Some(entry) => entry.value.as[YScalar].text
          case _           => node.as[YScalar].text
        }
      case _ => node.as[YScalar].text
    }

  protected def iri(node: YNode)(implicit ctx: GraphParserContext): AmfScalar = {
    val uri         = stringValue(node)
    val transformed = transformIdFromContext(uri)
    AmfScalar(transformed)
  }

  protected def bool(node: YNode)(implicit errorHandler: IllegalTypeHandler): AmfScalar = {
    val value = node.tagType match {
      case YType.Map =>
        node.as[YMap].entries.find(_.key.as[String] == JsonLdKeywords.Value) match {
          case Some(entry) => entry.value.as[YScalar].text.toBoolean
          case _           => node.as[YScalar].text.toBoolean
        }
      case _ => node.as[Boolean]
    }
    AmfScalar(value)
  }

  protected def int(node: YNode)(implicit errorHandler: IllegalTypeHandler): AmfScalar = {
    val value = node.tagType match {
      case YType.Map =>
        node.as[YMap].entries.find(_.key.as[String] == JsonLdKeywords.Value) match {
          case Some(entry) => entry.value.as[YScalar].text.toInt
          case _           => node.as[YScalar].text.toInt
        }
      case _ => node.as[YScalar].text.toInt
    }
    AmfScalar(value)
  }

  protected def long(node: YNode)(implicit errorHandler: IllegalTypeHandler): AmfScalar = {
    val value = node.tagType match {
      case YType.Map =>
        node.as[YMap].entries.find(_.key.as[String] == JsonLdKeywords.Value) match {
          case Some(entry) => entry.value.as[YScalar].text.toLong
          case _           => node.as[YScalar].text.toLong
        }
      case _ => node.as[YScalar].text.toLong
    }
    AmfScalar(value)
  }

  protected def date(node: YNode)(implicit errorHandler: IllegalTypeHandler): AmfScalar = {
    val value = node.tagType match {
      case YType.Map =>
        node.as[YMap].entries.find(_.key.as[String] == JsonLdKeywords.Value) match {
          case Some(entry) =>
            SimpleDateTime.parse(entry.value.as[YScalar].text).toOption.get
          case _ => SimpleDateTime.parse(node.as[YScalar].text).toOption.get
        }
      case _ => SimpleDateTime.parse(node.as[YScalar].text).toOption.get
    }
    AmfScalar(value)
  }

  protected def any(node: YNode)(implicit ctx: GraphParserContext): AmfScalar = {
    node.tagType match {
      case YType.Map =>
        val nodeValue =
          node.as[YMap].entries.find(_.key.as[String] == JsonLdKeywords.Value) match {
            case Some(entry) => entry.value.as[YScalar].text
            case _           => node.as[YScalar].text
          }
        node.as[YMap].entries.find(_.key.as[String] == JsonLdKeywords.Type) match {
          case Some(typeEntry) =>
            val typeUri     = typeEntry.value.as[YScalar].text
            val expandedUri = expandUriFromContext(typeUri)
            expandedUri match {
              case s: String if s == DataType.Boolean =>
                AmfScalar(nodeValue.toBoolean)
              case s: String if s == DataType.Integer => AmfScalar(nodeValue.toInt)
              case s: String if s == DataType.Float   => AmfScalar(nodeValue.toFloat)
              case s: String if s == DataType.Double  => AmfScalar(nodeValue.toDouble)
              case s: String if s == DataType.DateTime =>
                AmfScalar(SimpleDateTime.parse(nodeValue).toOption.get)
              case s: String if s == DataType.Date =>
                AmfScalar(SimpleDateTime.parse(nodeValue).toOption.get)
              case _ => AmfScalar(nodeValue)
            }
          case _ => AmfScalar(nodeValue)
        }
      case _ => AmfScalar(node.as[YScalar].text)
    }
  }

  def defineField(field: Field)(ctx: GraphParserContext): Option[TermDefinition] = {
    ctx.graphContext
      .definitions()
      .find { case (term, _) =>
        equal(term, field.value.iri())(ctx.graphContext)
      }
      .map { case (_, definition) =>
        definition
      }
  }

  def assertFieldTypeWithContext(field: Field)(ctx: GraphParserContext): Boolean = {
    val contextDefinition = defineField(field)(ctx)
    contextDefinition match {
      case Some(definition: ExpandedTermDefinition) =>
        assertFieldTypeWithDefinition(field, definition)(ctx)
      case _ => true
    }
  }

  private def assertFieldTypeWithDefinition(field: Field, definition: ExpandedTermDefinition)(
      ctx: GraphParserContext
  ) = {
    definition.`type`.forall { typeFromCtxDefinition =>
      val fieldTypes: immutable.Seq[ValueType] = field.`type`.`type`
      fieldTypes.exists(fieldType => equal(fieldType.iri(), typeFromCtxDefinition)(ctx.graphContext))
    }
  }

  protected def nodeIsOfType(node: YNode, obj: Obj)(implicit ctx: GraphParserContext): Boolean = {
    node.value match {
      case map: YMap => nodeIsOfType(map, obj)
      case _         => false
    }
  }

  protected def nodeIsOfType(map: YMap, obj: Obj)(implicit ctx: GraphParserContext): Boolean = {
    map.key(JsonLdKeywords.Type).exists { entry =>
      val types = entry.value.as[YSequence].nodes.flatMap(_.asScalar)
      types.exists(`type` => {
        val typeIri = expandUriFromContext(`type`.text)
        obj.`type`.map(_.iri()).contains(typeIri)
      })
    }
  }

  private def parseSourceNode(map: YMap)(implicit ctx: GraphParserContext): SourceMap = {
    val result = SourceMap()
    map.entries.foreach(entry => {
      entry.key.toOption[YScalar].map(value => expandUriFromContext(value.text)).foreach {
        case AnnotationName(annotation) =>
          val consumer = result.annotation(annotation)
          entry.value
            .as[Seq[YNode]]
            .foreach(e => {
              contentOfNode(e) foreach { element =>
                val k = element.key(compactUriFromContext(SourceMapModel.Element.value.iri())).get
                val v = element.key(compactUriFromContext(SourceMapModel.Value.value.iri())).get
                consumer(
                  value(SourceMapModel.Element.`type`, k.value).as[YScalar].text,
                  value(SourceMapModel.Value.`type`, v.value).as[YScalar].text
                )
              }
            })
        case _ => // Unknown annotation identifier
      }
    })
    result
  }

  private def asIris(ns: Namespace, elements: Seq[String]): Seq[ValueType] = elements.map(element => ns + element)

  // declared so they can be referenced from the retrieveType* functions
  private val amlDocumentIris: Seq[ValueType] =
    asIris(
      Namespace.Meta,
      Seq(
        "DialectInstance",
        "DialectInstanceFragment",
        "DialectInstanceLibrary",
        "DialectInstancePatch",
        "DialectLibrary",
        "DialectFragment",
        "Dialect",
        "Vocabulary"
      )
    )

  val coreDocumentIris: Seq[ValueType] =
    asIris(Namespace.Document, Seq("Document", "Fragment", "Module", "Unit"))

  val documentIris: Seq[ValueType] = amlDocumentIris ++ coreDocumentIris

  /** Returns a list a sequence of type from a YMap defined in the @type entry
    * @param map
    *   ymap input
    * @param id
    *   some id to throw an error if type retrieval fails
    * @param ctx
    *   graph parsing context
    * @return
    */
  protected def ts(map: YMap, id: String)(implicit ctx: GraphParserContext): Seq[String] = {
    val documentExpandedIris: Seq[String] = coreDocumentIris.map(docElement => docElement.iri())
    val documentCompactIris               = documentExpandedIris.map(compactUriFromContext(_))

    val documentTypesSet: Set[String] = (documentExpandedIris ++ documentCompactIris).toSet

    map.key(JsonLdKeywords.Type) match {
      case Some(entry) =>
        val nodes            = entry.value.toOption[Seq[YNode]].getOrElse(List(entry.value))
        val allTypes         = nodes.flatMap(v => v.toOption[YScalar].map(_.text))
        val nonDocumentTypes = allTypes.filter(t => !documentTypesSet.contains(t))
        val documentTypes =
          allTypes
            .filter(t => documentTypesSet.contains(t))
            .sorted // we just use the fact that lexical order is correct
        nonDocumentTypes ++ documentTypes

      case _ =>
        ctx.eh.violation(MissingTypeInNode, id, s"No @type declaration on node $map", map.location)
        Nil
    }
  }

  protected def retrieveId(map: YMap, ctx: ParserContext): Option[String] = {
    implicit val errorHandler: AMFErrorHandler = ctx.eh

    map.key(JsonLdKeywords.Id) match {
      case Some(entry) => Some(entry.value.as[YScalar].text)
      case _ =>
        ctx.eh.violation(MissingIdInNode, "", s"No @id declaration on node $map", map.location)
        None
    }
  }

  protected def contentOfNode(n: YNode): Option[YMap] = n.toOption[YMap]

  protected def retrieveSources(map: YMap)(implicit ctx: GraphParserContext): SourceMap = {
    map
      .key(compactUriFromContext(DomainElementModel.Sources.value.iri()))
      .flatMap { entry =>
        val srcNode = value(SourceMapModel, entry.value)
        contentOfNode(srcNode).map(parseSourceNode(_))
      }
      .getOrElse(SourceMap.empty)
  }

  protected def value(t: Type, node: YNode)(implicit eh: IllegalTypeHandler): YNode = {
    node.tagType match {
      case YType.Seq =>
        t match {
          case Array(_) => node
          case _        => value(t, node.as[Seq[YNode]].head)
        }
      case YType.Map =>
        val m: YMap = node.as[YMap]
        t match {
          case Type.Any                       => m.key(JsonLdKeywords.Value).orElse(m.key(JsonLdKeywords.Id)).get.value
          case Iri                            => m.key(JsonLdKeywords.Id).get.value
          case Str | RegExp | Bool | Type.Int => m.key(JsonLdKeywords.Value).get.value
          case _                              => node
        }
      case _ => node
    }
  }

  protected object AnnotationName {
    def unapply(uri: String): Option[String] = uri match {
      case url if url.startsWith(SourceMaps.base) => Some(url.substring(url.indexOf("#") + 1))
      case _                                      => None
    }
  }

}

abstract class GraphContextHelper extends GraphContextOperations {

  protected def expandUriFromContext(iri: String)(implicit ctx: GraphParserContext): String = {
    expand(iri)(ctx.graphContext)
  }

  protected def compactUriFromContext(iri: String)(implicit ctx: GraphParserContext): String = {
    compact(iri)(ctx.graphContext)
  }

  protected def transformIdFromContext(iri: String)(implicit ctx: GraphParserContext): String = {
    IriClassification.classify(iri) match {
      case AbsoluteIri => iri
      case RelativeIri => resolveWithBase(iri, ctx)
    }
  }

  protected def adaptUriToContext(iri: String)(implicit ctx: GraphParserContext): String = {
    IriClassification.classify(iri) match {
      case AbsoluteIri => iri.stripPrefix(getPrefixOption(iri, ctx).getOrElse(""))
      case RelativeIri => iri
    }
  }

  private def getPrefixOption(id: String, ctx: GraphParserContext): Option[String] = {
    ctx.graphContext.base.map { base =>
      if (id.startsWith("./")) base.parent.iri + "/"
      else base.iri
    }
  }

  private def resolveWithBase(id: String, ctx: GraphParserContext): String = {
    val prefixOption = getPrefixOption(id, ctx)
    val prefix       = prefixOption.getOrElse("")
    s"$prefix$id"
  }

  protected def asSeq[T](opt: Option[T]): Seq[T] = opt match {
    case Some(v) => Seq(v)
    case None    => Nil
  }
}
