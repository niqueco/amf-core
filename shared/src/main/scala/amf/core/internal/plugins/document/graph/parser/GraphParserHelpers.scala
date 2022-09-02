package amf.core.internal.plugins.document.graph.parser

import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.DataType
import amf.core.client.scala.model.document.SourceMap
import amf.core.client.scala.model.domain._
import amf.core.client.scala.model.domain.extensions.{CustomDomainProperty, DomainExtension}
import amf.core.client.scala.parse.document.ParserContext
import amf.core.client.scala.vocabulary.Namespace.SourceMaps
import amf.core.client.scala.vocabulary._
import amf.core.internal.annotations.DomainExtensionAnnotation
import amf.core.internal.metamodel.Type._
import amf.core.internal.metamodel.document.SourceMapModel
import amf.core.internal.metamodel.domain.DomainElementModel
import amf.core.internal.metamodel.domain.extensions.DomainExtensionModel
import amf.core.internal.metamodel.{Field, ModelDefaultBuilder, Type}
import amf.core.internal.parser._
import amf.core.internal.parser.domain.{Annotations, FieldEntry}
import amf.core.internal.plugins.document.graph.JsonLdKeywords
import amf.core.internal.plugins.document.graph.context.{ExpandedTermDefinition, GraphContext, TermDefinition}
import amf.core.internal.validation.CoreValidations.{MissingIdInNode, MissingTypeInNode, NotLinkable}
import org.mulesoft.common.time.SimpleDateTime
import org.yaml.convert.YRead.SeqNodeYRead
import org.yaml.model._

import scala.collection.{immutable, mutable}

abstract class GraphParserHelpers(val nodes: mutable.Map[String, AmfElement])(implicit ctx: GraphParserContext) extends GraphContextHelper {
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
            SimpleDateTime.parse(entry.value.as[YScalar].text).right.get
          case _ => SimpleDateTime.parse(node.as[YScalar].text).right.get
        }
      case _ => SimpleDateTime.parse(node.as[YScalar].text).right.get
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
                AmfScalar(SimpleDateTime.parse(nodeValue).right.get)
              case s: String if s == DataType.Date =>
                AmfScalar(SimpleDateTime.parse(nodeValue).right.get)
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

  def asIris(ns: Namespace, elements: Seq[String]): Seq[ValueType] = elements.map(element => ns + element)

  // declared so they can be referenced from the retrieveType* functions
  val amlDocumentIris: Seq[ValueType] =
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

  val referencesMap: mutable.Map[String, DomainElement] = mutable.Map[String, DomainElement]()

  val unresolvedReferences: mutable.Map[String, Seq[DomainElement]] = mutable.Map[String, Seq[DomainElement]]()

  val unresolvedExtReferencesMap: mutable.Map[String, ExternalSourceElement] =
    mutable.Map[String, ExternalSourceElement]()

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

  protected def checkLinkables(instance: AmfObject): Unit = {
    instance match {
      case link: DomainElement with Linkable =>
        referencesMap += (link.id -> link)
        unresolvedReferences.getOrElse(link.id, Nil).foreach {
          case unresolved: Linkable =>
            unresolved.withLinkTarget(link)
          case unresolved: LinkNode =>
            unresolved.withLinkedDomainElement(link)
          case _ =>
            ctx.eh.violation(NotLinkable, instance.id, "Only linkable elements can be linked", instance.annotations)
        }
        unresolvedReferences.update(link.id, Nil)
      case _ => // ignore
    }

    instance match {
      case ref: ExternalSourceElement =>
        unresolvedExtReferencesMap += (ref.referenceId.value -> ref) // process when parse the references node
      case _ => // ignore
    }
  }

  protected def setLinkTarget(instance: DomainElement with Linkable, targetId: String): Unit = {
    referencesMap.get(targetId) match {
      case Some(target) => instance.withLinkTarget(target)
      case None =>
        val unresolved: Seq[DomainElement] =
          unresolvedReferences.getOrElse(targetId, Nil)
        unresolvedReferences += (targetId -> (unresolved ++ Seq(instance)))
    }
  }

  protected def annotations(nodes: mutable.Map[String, AmfElement], sources: SourceMap, key: String): Annotations =
    ctx.config.serializableAnnotationsFacade.retrieveAnnotation(nodes.toMap, sources, key)

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

  protected def parseCustomProperties(map: YMap, instance: DomainElement): Unit = {
    val properties = map
      .key(compactUriFromContext(DomainElementModel.CustomDomainProperties.value.iri()))
      .map(_.value.as[Seq[YNode]].map(value(Iri, _).as[YScalar].text))
      .getOrElse(Nil)

    val extensions = properties
      .flatMap { uri =>
        map
          .key(
            transformIdFromContext(uri)
          ) // See ADR adrs/0006-custom-domain-properties-json-ld-rendering.md last consequence item
          .map(entry => {
            val extension = DomainExtension()
            val obj       = mapValueFrom(entry)

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

            val sources = retrieveSources(map)
            extension.annotations ++= annotations(nodes, sources, extension.id)

            extension
          })
      }

    if (extensions.nonEmpty) {
      extensions.partition(_.isScalarExtension) match {
        case (scalars, objects) =>
          instance.withCustomDomainProperties(objects)
          applyScalarDomainProperties(instance, scalars)
      }
    }
  }

  protected def mapValueFrom(entry: YMapEntry): YMap


  protected def buildType(modelType: ModelDefaultBuilder, ann: Annotations): AmfObject = {
    val instance = modelType.modelInstance
    instance.annotations ++= ann
    instance
  }

  protected def parseScalarProperty(definition: YMap, field: Field): Option[String] =
    definition
      .key(compactUriFromContext(field.value.iri()))
      .map(entry => value(field.`type`, entry.value).as[YScalar].text)

  protected def parse(map: YMap): Option[AmfObject]

}
