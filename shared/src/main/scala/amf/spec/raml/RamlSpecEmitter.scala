package amf.spec.raml

import amf.common.AMFToken._
import amf.common.TSort.tsort
import amf.common.{AMFAST, AMFToken}
import amf.document.{BaseUnit, Document}
import amf.domain.Annotation._
import amf.domain._
import amf.maker.BaseUriSplitter
import amf.metadata.Field
import amf.metadata.domain._
import amf.metadata.shape.{NodeShapeModel, ScalarShapeModel, ShapeModel}
import amf.model.AmfScalar
import amf.parser.Position.ZERO
import amf.parser.{AMFASTFactory, ASTEmitter, Position}
import amf.remote.{Oas, Raml, Vendor}
import amf.shape._
import amf.spec.SpecOrdering.ordering
import amf.spec.{Emitter, SpecOrdering}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by pedro.colunga on 8/17/17.
  */
case class RamlSpecEmitter(unit: BaseUnit) {

  val emitter: ASTEmitter[AMFToken, AMFAST] = ASTEmitter(AMFASTFactory())

  private def retrieveWebApi() = unit match {
    case document: Document => document.encodes
  }

  def emitWebApi(): AMFAST = {
    val model  = retrieveWebApi()
    val vendor = model.annotations.find(classOf[SourceVendor]).map(_.vendor)
    val api    = WebApiEmitter(model, ordering(Raml, model.annotations), vendor)

    emitter.root(Root) { () =>
      raw("%RAML 1.0", Comment)
      map { () =>
        traverse(api.emitters)
      }
    }
  }

  private def traverse(emitters: Seq[Emitter]): Unit = {
    emitters.foreach(e => {
      e.emit()
    })
  }

  private def entry(inner: () => Unit): Unit = node(Entry)(inner)

  private def array(inner: () => Unit): Unit = node(SequenceToken)(inner)

  private def map(inner: () => Unit): Unit = node(MapToken)(inner)

  private def node(t: AMFToken)(inner: () => Unit) = {
    emitter.beginNode()
    inner()
    emitter.endNode(t)
  }

  private def raw(content: String, token: AMFToken = StringToken): Unit = {
    emitter.value(token, content)
  }

  case class WebApiEmitter(api: WebApi, ordering: SpecOrdering, vendor: Option[Vendor]) {

    val emitters: Seq[Emitter] = {
      val fs     = api.fields
      val result = mutable.ListBuffer[Emitter]()

      fs.entry(WebApiModel.Name).map(f => result += ValueEmitter("title", f))

      fs.entry(WebApiModel.BaseUriParameters).map(f => result += ParametersEmitter("baseUriParameters", f, ordering))

      fs.entry(WebApiModel.Description).map(f => result += ValueEmitter("description", f))

      fs.entry(WebApiModel.ContentType)
        .map(f => {
          if (f.value.annotations.contains(classOf[SingleValueArray])) result += ArrayValueEmitter("mediaType", f)
          else result += ArrayEmitter("mediaType", f, ordering)
        })

      fs.entry(WebApiModel.Version).map(f => result += ValueEmitter("version", f))

      fs.entry(WebApiModel.TermsOfService).map(f => result += ValueEmitter("(termsOfService)", f))

      fs.entry(WebApiModel.Schemes)
        .filter(!_.value.annotations.contains(classOf[SynthesizedField]))
        .map(f => result += ArrayEmitter("protocols", f, ordering))

      fs.entry(WebApiModel.Provider).map(f => result += OrganizationEmitter("(contact)", f, ordering))

      fs.entry(WebApiModel.Documentation).map(f => result += CreativeWorkEmitter("(externalDocs)", f, ordering))

      fs.entry(WebApiModel.License).map(f => result += LicenseEmitter("(license)", f, ordering))

      fs.entry(WebApiModel.EndPoints).map(f => result ++= endpoints(f, ordering, vendor))

      result += BaseUriEmitter(fs)

      ordering.sorted(result)
    }

    private def endpoints(f: FieldEntry, ordering: SpecOrdering, vendor: Option[Vendor]): Seq[Emitter] = {
      val endpoints = f.array.values
        .asInstanceOf[Seq[EndPoint]]

      val notOas = !vendor.contains(Oas)

      if (notOas) {
        val graph                                       = endpoints.map(e => (e, e.parent.toSet)).toMap
        val all: mutable.Map[EndPoint, EndPointEmitter] = mutable.ListMap()
        tsort(graph, Seq()).foreach(e => {
          val emitter = EndPointEmitter(e, ordering)
          e.parent match {
            case Some(parent) =>
              all(parent) += emitter
              all += (e -> emitter)
            case _ => all += (e -> emitter)
          }
        })
        all.filterKeys(_.parent.isEmpty).values.toSeq
      } else {
        endpoints.map(EndPointEmitter(_, ordering))
      }
    }

    private case class BaseUriEmitter(fs: Fields) extends Emitter {
      override def emit(): Unit = {
        val protocol: String = fs
          .entry(WebApiModel.Schemes)
          .find(_.value.annotations.contains(classOf[SynthesizedField]))
          .flatMap(_.array.values.headOption)
          .map(_.asInstanceOf[AmfScalar].toString)
          .getOrElse("")

        val domain: String = fs
          .entry(WebApiModel.Host)
          .map(_.scalar.value)
          .map(_.toString)
          .getOrElse("")

        val basePath: String = fs
          .entry(WebApiModel.BasePath)
          .map(_.scalar.value)
          .map(_.toString)
          .getOrElse("")

        val uri = BaseUriSplitter(protocol, domain, basePath)

        if (uri.nonEmpty) {
          entry { () =>
            raw("baseUri")
            raw(uri.url())
          }
        }
      }

      override def position(): Position =
        fs.entry(WebApiModel.BasePath)
          .flatMap(f => f.value.annotations.find(classOf[LexicalInformation]))
          .orElse(fs.entry(WebApiModel.Host).flatMap(f => f.value.annotations.find(classOf[LexicalInformation])))
          .orElse(
            fs.entry(WebApiModel.Schemes)
              .find(_.value.annotations.contains(classOf[SynthesizedField]))
              .flatMap(f => f.value.annotations.find(classOf[LexicalInformation])))
          .map(_.range.start)
          .getOrElse(ZERO)
    }
  }

  case class ArrayEmitter(key: String, f: FieldEntry, ordering: SpecOrdering) extends Emitter {
    override def emit(): Unit = {
      sourceOr(
        f.value,
        entry { () =>
          raw(key)

          val result = mutable.ListBuffer[Emitter]()

          f.array.values
            .foreach(v => {
              result += ScalarEmitter(v.asInstanceOf[AmfScalar])
            })

          array { () =>
            traverse(ordering.sorted(result))
          }
        }
      )
    }

    override def position(): Position = pos(f.value.annotations)
  }

  case class EndPointEmitter(endpoint: EndPoint,
                             ordering: SpecOrdering,
                             children: mutable.ListBuffer[EndPointEmitter] = mutable.ListBuffer())
      extends Emitter {

    override def emit(): Unit = {
      sourceOr(
        endpoint.annotations,
        entry { () =>
          val fs = endpoint.fields

          endpoint.parent.fold(ScalarEmitter(fs.entry(EndPointModel.Path).get.scalar).emit())(_ =>
            ScalarEmitter(AmfScalar(endpoint.relativePath)).emit())

          val result = mutable.ListBuffer[Emitter]()

          fs.entry(EndPointModel.Name).map(f => result += ValueEmitter("displayName", f))

          fs.entry(EndPointModel.Description).map(f => result += ValueEmitter("description", f))

          fs.entry(EndPointModel.UriParameters).map(f => result += ParametersEmitter("uriParameters", f, ordering))

          fs.entry(EndPointModel.Operations).map(f => result ++= operations(f, ordering))

          result ++= children

          map { () =>
            traverse(ordering.sorted(result))
          }
        }
      )
    }

    def +=(child: EndPointEmitter): Unit = children += child

    private def operations(f: FieldEntry, ordering: SpecOrdering): Seq[Emitter] = {
      f.array.values
        .map(e => OperationEmitter(e.asInstanceOf[Operation], ordering))
    }

    override def position(): Position = pos(endpoint.annotations)
  }

  case class OperationEmitter(operation: Operation, ordering: SpecOrdering) extends Emitter {
    override def emit(): Unit = {
      sourceOr(
        operation.annotations,
        entry { () =>
          val fs = operation.fields

          ScalarEmitter(fs.entry(OperationModel.Method).get.scalar).emit()

          val result = mutable.ListBuffer[Emitter]()

          fs.entry(OperationModel.Name).map(f => result += ValueEmitter("displayName", f))

          fs.entry(OperationModel.Description).map(f => result += ValueEmitter("description", f))

          fs.entry(OperationModel.Deprecated).map(f => result += ValueEmitter("(deprecated)", f))

          fs.entry(OperationModel.Summary).map(f => result += ValueEmitter("(summary)", f))

          fs.entry(OperationModel.Documentation).map(f => result += CreativeWorkEmitter("(externalDocs)", f, ordering))

          fs.entry(OperationModel.Schemes).map(f => result += ArrayEmitter("protocols", f, ordering))

          Option(operation.request).foreach(req => {
            val reqFs = req.fields

            reqFs
              .entry(RequestModel.QueryParameters)
              .map(f => result += ParametersEmitter("queryParameters", f, ordering))

            reqFs.entry(RequestModel.Headers).map(f => result += ParametersEmitter("headers", f, ordering))

            reqFs.entry(RequestModel.Payloads).map(f => result += PayloadsEmitter("body", f, ordering))
          })

          fs.entry(OperationModel.Responses).map(f => result += ResponsesEmitter("responses", f, ordering))

          map { () =>
            traverse(ordering.sorted(result))
          }
        }
      )
    }

    override def position(): Position = pos(operation.annotations)
  }

  case class ResponsesEmitter(key: String, f: FieldEntry, ordering: SpecOrdering) extends Emitter {
    override def emit(): Unit = {
      sourceOr(
        f.value.annotations,
        entry { () =>
          raw(key)

          map { () =>
            traverse(responses(f, ordering))
          }
        }
      )
    }

    private def responses(f: FieldEntry, ordering: SpecOrdering): Seq[Emitter] = {
      val result = mutable.ListBuffer[Emitter]()
      f.array.values
        .foreach(e => result += ResponseEmitter(e.asInstanceOf[Response], ordering))
      ordering.sorted(result)
    }

    override def position(): Position = pos(f.value.annotations)
  }

  case class ResponseEmitter(response: Response, ordering: SpecOrdering) extends Emitter {
    override def emit(): Unit = {
      sourceOr(
        response.annotations,
        entry { () =>
          val result = mutable.ListBuffer[Emitter]()
          val fs     = response.fields

          ScalarEmitter(fs.entry(ResponseModel.StatusCode).get.scalar).emit()

          fs.entry(ResponseModel.Description).map(f => result += ValueEmitter("description", f))

          fs.entry(RequestModel.Headers).map(f => result += ParametersEmitter("headers", f, ordering))

          fs.entry(RequestModel.Payloads).map(f => result += PayloadsEmitter("body", f, ordering))

          map { () =>
            traverse(ordering.sorted(result))
          }
        }
      )
    }

    override def position(): Position = pos(response.annotations)
  }

  case class PayloadsEmitter(key: String, f: FieldEntry, ordering: SpecOrdering) extends Emitter {
    override def emit(): Unit = {
      sourceOr(
        f.value.annotations,
        entry { () =>
          raw(key)

          map { () =>
            traverse(payloads(f, ordering))
          }
        }
      )
    }

    private def payloads(f: FieldEntry, ordering: SpecOrdering): Seq[Emitter] = {
      val result = mutable.ListBuffer[Emitter]()
      f.array.values
        .foreach(e => result ++= Payloads(e.asInstanceOf[Payload], ordering).emitters())
      ordering.sorted(result)
    }

    override def position(): Position = pos(f.value.annotations)
  }

  case class Payloads(payload: Payload, ordering: SpecOrdering) {
    def emitters(): Seq[Emitter] = {
      val fs = payload.fields
      fs.entry(PayloadModel.MediaType)
        .fold(
          RamlTypeEmitter(payload.schema, ordering).emitters()
        )(_ => Seq(PayloadEmitter(payload, ordering)))
    }
  }

  case class PayloadEmitter(payload: Payload, ordering: SpecOrdering) extends Emitter {
    override def emit(): Unit = {
      val fs = payload.fields
      fs.entry(PayloadModel.MediaType)
        .foreach(mediaType => {
          entry { () =>
            ScalarEmitter(mediaType.scalar).emit()
            val result = RamlTypeEmitter(payload.schema, ordering).emitters()

            map { () =>
              traverse(ordering.sorted(result))
            }
          }
        })
    }

    override def position(): Position = pos(payload.annotations)
  }

  case class ParametersEmitter(key: String, f: FieldEntry, ordering: SpecOrdering) extends Emitter {
    override def emit(): Unit = {
      sourceOr(
        f.value.annotations,
        entry { () =>
          raw(key)

          map { () =>
            traverse(parameters(f, ordering))
          }
        }
      )
    }

    private def parameters(f: FieldEntry, ordering: SpecOrdering): Seq[Emitter] = {
      val result = mutable.ListBuffer[Emitter]()
      f.array.values
        .foreach(e => result += ParameterEmitter(e.asInstanceOf[Parameter], ordering))
      ordering.sorted(result)
    }

    override def position(): Position = pos(f.value.annotations)
  }

  case class ParameterEmitter(parameter: Parameter, ordering: SpecOrdering) extends Emitter {
    override def emit(): Unit = {
      sourceOr(
        parameter.annotations,
        entry { () =>
          val fs = parameter.fields

          val explicit = fs
            .entry(ParameterModel.Required)
            .exists(_.value.annotations.contains(classOf[ExplicitField]))

          if (!explicit && !parameter.required) {
            ScalarEmitter(AmfScalar(parameter.name + "?")).emit()
          } else {
            val name = fs.entry(ParameterModel.Name).get.scalar
            ScalarEmitter(name).emit()
          }

          val result = mutable.ListBuffer[Emitter]()

          fs.entry(ParameterModel.Description).map(f => result += ValueEmitter("description", f))

          fs.entry(ParameterModel.Required)
            .filter(_.value.annotations.contains(classOf[ExplicitField]))
            .map(f => result += ValueEmitter("required", f))

          result ++= RamlTypeEmitter(parameter.schema, ordering, Seq(ShapeModel.Description)).emitters()

          map { () =>
            traverse(ordering.sorted(result))
          }
        }
      )
    }

    override def position(): Position = pos(parameter.annotations)
  }

  case class LicenseEmitter(key: String, f: FieldEntry, ordering: SpecOrdering) extends Emitter {
    override def emit(): Unit = {
      sourceOr(
        f.value,
        entry { () =>
          raw(key)

          val fs     = f.obj.fields
          val result = mutable.ListBuffer[Emitter]()

          fs.entry(LicenseModel.Url).map(f => result += ValueEmitter("url", f))

          fs.entry(LicenseModel.Name).map(f => result += ValueEmitter("name", f))

          map { () =>
            traverse(ordering.sorted(result))
          }
        }
      )
    }

    override def position(): Position = pos(f.value.annotations)
  }

  case class OrganizationEmitter(key: String, f: FieldEntry, ordering: SpecOrdering) extends Emitter {
    override def emit(): Unit = {
      sourceOr(
        f.value,
        entry { () =>
          raw(key)

          val fs     = f.obj.fields
          val result = mutable.ListBuffer[Emitter]()

          fs.entry(OrganizationModel.Url).map(f => result += ValueEmitter("url", f))

          fs.entry(OrganizationModel.Name).map(f => result += ValueEmitter("name", f))

          fs.entry(OrganizationModel.Email).map(f => result += ValueEmitter("email", f))

          map { () =>
            traverse(ordering.sorted(result))
          }
        }
      )
    }

    override def position(): Position = pos(f.value.annotations)
  }

  case class CreativeWorkEmitter(key: String, f: FieldEntry, ordering: SpecOrdering) extends Emitter {
    override def emit(): Unit = {
      sourceOr(
        f.value,
        entry { () =>
          raw(key)

          val fs     = f.obj.fields
          val result = mutable.ListBuffer[Emitter]()

          fs.entry(CreativeWorkModel.Url).map(f => result += ValueEmitter("url", f))

          fs.entry(CreativeWorkModel.Description).map(f => result += ValueEmitter("description", f))

          map { () =>
            traverse(ordering.sorted(result))
          }
        }
      )
    }

    override def position(): Position = pos(f.value.annotations)
  }

  case class ScalarEmitter(v: AmfScalar) extends Emitter {
    override def emit(): Unit = sourceOr(v.annotations, raw(v.toString))

    override def position(): Position = pos(v.annotations)
  }

  case class ValueEmitter(key: String, f: FieldEntry) extends Emitter {
    override def emit(): Unit = {
      sourceOr(f.value, entry { () =>
        raw(key)
        raw(f.scalar.toString)
      })
    }

    override def position(): Position = pos(f.value.annotations)
  }

  case class EntryEmitter(key: String,
                          value: String,
                          token: AMFToken = StringToken,
                          position: Position = Position.ZERO)
      extends Emitter {
    override def emit(): Unit = {
      entry { () =>
        raw(key)
        raw(value, token)
      }
    }
  }

  /** Emit a single value from an array as an entry. */
  case class ArrayValueEmitter(key: String, f: FieldEntry) extends Emitter {
    override def emit(): Unit = {
      sourceOr(f.value, entry { () =>
        raw(key)
        raw(f.array.values.headOption.map(_.asInstanceOf[AmfScalar].toString).getOrElse(""))
      })
    }

    override def position(): Position = pos(f.value.annotations)
  }

  private def pos(annotations: Annotations): Position =
    annotations.find(classOf[LexicalInformation]).map(_.range.start).getOrElse(ZERO)

  private def sourceOr(value: Value, inner: => Unit): Unit = sourceOr(value.annotations, inner)

  private def sourceOr(annotations: Annotations, inner: => Unit): Unit = {
    //    annotations
    //      .find(classOf[SourceAST])
    //      .fold(inner)(a => emitter.addChild(a.ast))
    inner
  }

  case class RamlTypeEmitter(shape: Shape, ordering: SpecOrdering, ignored: Seq[Field] = Nil) {
    def emitters(): Seq[Emitter] = {
      shape match {
        case node: NodeShape =>
          val copiedNode = node.copy(fields = node.fields.filter(f => !ignored.contains(f._1)))
          NodeShapeEmitter(copiedNode, ordering).emitters()
        case scalar: ScalarShape =>
          val copiedScalar = scalar.copy(fields = scalar.fields.filter(f => !ignored.contains(f._1)))
          ScalarShapeEmitter(copiedScalar, ordering).emitters()
        case _ => Seq()
      }
    }
  }

  abstract class ShapeEmitter(shape: Shape, ordering: SpecOrdering) {
    def emitters(): Seq[Emitter] = {

      val result = ListBuffer[Emitter]()
      val fs     = shape.fields

      fs.entry(ShapeModel.DisplayName).map(f => result += ValueEmitter("displayName", f))

      fs.entry(ShapeModel.Description).map(f => result += ValueEmitter("description", f))

      fs.entry(ShapeModel.Default).map(f => result += ValueEmitter("default", f))

      fs.entry(ShapeModel.Values).map(f => result += ValueEmitter("enum", f))

      fs.entry(ShapeModel.Documentation).map(f => result += CreativeWorkEmitter("(externalDocs)", f, ordering))

      result
    }
  }

  case class NodeShapeEmitter(node: NodeShape, ordering: SpecOrdering) extends ShapeEmitter(node, ordering) {
    override def emitters(): Seq[Emitter] = {
      val result: ListBuffer[Emitter] = ListBuffer[Emitter]() ++ super.emitters()

      val fs = node.fields

      // TODO annotation for original position?
      if (node.annotations.contains(classOf[ExplicitField]))
        result += EntryEmitter("type", "object")

      fs.entry(NodeShapeModel.MinProperties).map(f => result += ValueEmitter("minProperties", f))

      fs.entry(NodeShapeModel.MaxProperties).map(f => result += ValueEmitter("maxProperties", f))

      fs.entry(NodeShapeModel.Closed)
        .filter(_.value.annotations.contains(classOf[ExplicitField]))
        .map(f =>
          result += EntryEmitter("additionalProperties", (!node.closed).toString, position = pos(f.value.annotations)))

      fs.entry(NodeShapeModel.Discriminator).map(f => result += ValueEmitter("discriminator", f))

      fs.entry(NodeShapeModel.DiscriminatorValue).map(f => result += ValueEmitter("discriminatorValue", f))

      fs.entry(NodeShapeModel.ReadOnly).map(f => result += ValueEmitter("(readOnly)", f))

      fs.entry(NodeShapeModel.Properties).map(f => result += PropertiesShapeEmitter(f, ordering))

      result
    }

  }

  case class ScalarShapeEmitter(scalar: ScalarShape, ordering: SpecOrdering) extends ShapeEmitter(scalar, ordering) {
    override def emitters(): Seq[Emitter] = {
      val result: ListBuffer[Emitter] = ListBuffer[Emitter]() ++ super.emitters()

      val fs = scalar.fields

      val (typeDef, format) = RamlTypeDefStringValueMatcher.matchType(TypeDefXsdMapping.typeDef(scalar.dataType)) // TODO Check this

      fs.entry(ScalarShapeModel.DataType)
        .map(
          f =>
            result += EntryEmitter(
              "type",
              typeDef,
              position =
                if (f.value.annotations.contains(classOf[Inferred])) Position.ZERO
                else pos(f.value.annotations))) // TODO check this  - annotations of typeDef in parser

      fs.entry(ScalarShapeModel.Pattern).map(f => result += ValueEmitter("pattern", f))

      fs.entry(ScalarShapeModel.MinLength).map(f => result += ValueEmitter("minLength", f))

      fs.entry(ScalarShapeModel.MaxLength).map(f => result += ValueEmitter("maxLength", f))

      fs.entry(ScalarShapeModel.Minimum).map(f => result += ValueEmitter("minimum", f))

      fs.entry(ScalarShapeModel.Maximum).map(f => result += ValueEmitter("maximum", f))

      fs.entry(ScalarShapeModel.ExclusiveMinimum).map(f => result += ValueEmitter("(exclusiveMinimum)", f))

      fs.entry(ScalarShapeModel.ExclusiveMaximum).map(f => result += ValueEmitter("(exclusiveMaximum)", f))

      fs.entry(ScalarShapeModel.MultipleOf).map(f => result += ValueEmitter("multipleOf", f))

      if (format.nonEmpty) result += EntryEmitter("(format)", format)
      else fs.entry(ScalarShapeModel.Format).map(f => result += ValueEmitter("format", f)) // todo mutually exclusive?

      result
    }
  }

  case class PropertiesShapeEmitter(f: FieldEntry, ordering: SpecOrdering) extends Emitter {
    def emit(): Unit = {

      entry { () =>
        raw("properties")
        map { () =>
          val result = f.array.values.map(v => PropertyShapeEmitter(v.asInstanceOf[PropertyShape], ordering))
          traverse(ordering.sorted(result))
        }
      }
    }

    override def position(): Position = pos(f.value.annotations)
  }

  case class PropertyShapeEmitter(property: PropertyShape, ordering: SpecOrdering) extends Emitter {

    def emit(): Unit = {
      entry { () =>
        raw(property.name)
        map { () =>
          traverse(ordering.sorted(RamlTypeEmitter(property.range, ordering).emitters()))

        }
      }
    }

    override def position(): Position = pos(property.annotations) // TODO check this
  }
}
