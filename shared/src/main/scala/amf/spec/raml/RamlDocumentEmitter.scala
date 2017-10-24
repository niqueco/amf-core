package amf.spec.raml

import amf.common.TSort.tsort
import amf.compiler.RamlHeader
import amf.document.Fragment.{ExtensionFragment, OverlayFragment}
import amf.document.{BaseUnit, Document, Module}
import amf.domain.Annotation._
import amf.domain._
import amf.domain.extensions.{
  ArrayNode => DataArrayNode,
  ObjectNode => DataObjectNode,
  ScalarNode => DataScalarNode,
  _
}
import amf.metadata.domain._
import amf.model.AmfScalar
import amf.parser.Position
import amf.parser.Position.ZERO
import amf.remote.{Oas, Raml, Vendor}
import amf.shape._
import amf.spec._
import amf.spec.common.BaseEmitters._
import amf.spec.common.{BaseSpecEmitter, SpecEmitterContext}
import amf.spec.declaration._
import amf.spec.domain._
import org.yaml.model.YDocument.{EntryBuilder, PartBuilder}
import org.yaml.model.{YDocument, YNode, YScalar, YType}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class RamlDocumentEmitter(document: BaseUnit) extends RamlSpecEmitter {

  private def retrieveWebApi(): WebApi = document match {
    case document: Document           => document.encodes.asInstanceOf[WebApi]
    case extension: ExtensionFragment => extension.encodes
    case overlay: OverlayFragment     => overlay.encodes
    case _                            => throw new Exception("BaseUnit doesn't encode a WebApi.")
  }

  def emitDocument(): YDocument = {
    val doc                    = document.asInstanceOf[Document]
    val ordering: SpecOrdering = SpecOrdering.ordering(Raml, doc.encodes.annotations)

    val api        = apiEmitters(ordering, doc.references)
    val declares   = DeclarationsEmitter(doc.declares, doc.references, ordering).emitters
    val references = ReferencesEmitter(doc.references, ordering)

    YDocument(b => {
      b.comment(RamlHeader.Raml10.text)
      b.map { b =>
        traverse(ordering.sorted(api ++ declares :+ references), b)
      }
    })
  }

  def apiEmitters(ordering: SpecOrdering, references: Seq[BaseUnit]): Seq[EntryEmitter] = {
    val model  = retrieveWebApi()
    val vendor = model.annotations.find(classOf[SourceVendor]).map(_.vendor)
    WebApiEmitter(model, ordering, vendor, references).emitters
  }

  case class WebApiEmitter(api: WebApi,
                           ordering: SpecOrdering,
                           vendor: Option[Vendor],
                           references: Seq[BaseUnit] = Seq()) {

    val emitters: Seq[EntryEmitter] = {
      val fs     = api.fields
      val result = mutable.ListBuffer[EntryEmitter]()

      fs.entry(WebApiModel.Name).map(f => result += ValueEmitter("title", f))

      fs.entry(WebApiModel.BaseUriParameters)
        .map(f => result += RamlParametersEmitter("baseUriParameters", f, ordering, references))

      fs.entry(WebApiModel.Description).map(f => result += ValueEmitter("description", f))

      fs.entry(WebApiModel.ContentType).map(f => result += ArrayEmitter("mediaType", f, ordering))

      fs.entry(WebApiModel.Version).map(f => result += ValueEmitter("version", f))

      fs.entry(WebApiModel.TermsOfService).map(f => result += ValueEmitter("(termsOfService)", f))

      fs.entry(WebApiModel.Schemes)
        .filter(!_.value.annotations.contains(classOf[SynthesizedField]))
        .map(f => result += ArrayEmitter("protocols", f, ordering))

      fs.entry(WebApiModel.Provider).map(f => result += OrganizationEmitter("(contact)", f, ordering))

      fs.entry(WebApiModel.Documentations).map(f => result += UserDocumentationsEmitter(f, ordering))

      fs.entry(WebApiModel.License).map(f => result += LicenseEmitter("(license)", f, ordering))

      fs.entry(WebApiModel.EndPoints).map(f => result ++= endpoints(f, ordering, vendor))

      result += BaseUriEmitter(fs)

      result ++= AnnotationsEmitter(api, ordering).emitters

      fs.entry(WebApiModel.Security).map(f => result += ParametrizedSecuritiesSchemeEmitter("securedBy", f, ordering))

      ordering.sorted(result)
    }

    private def endpoints(f: FieldEntry, ordering: SpecOrdering, vendor: Option[Vendor]): Seq[EntryEmitter] = {

      def defaultOrder(emitters: Seq[EndPointEmitter]): Seq[EndPointEmitter] = {
        emitters.sorted((x: EndPointEmitter, y: EndPointEmitter) =>
          x.endpoint.path.count(_ == '/') compareTo y.endpoint.path.count(_ == '/'))
      }

      val endpoints = f.array.values
        .asInstanceOf[Seq[EndPoint]]

      val notOas = !vendor.contains(Oas)

      if (notOas) {
        val graph                                           = endpoints.map(e => (e, e.parent.toSet)).toMap
        val all: mutable.ListMap[EndPoint, EndPointEmitter] = mutable.ListMap[EndPoint, EndPointEmitter]()
        tsort(graph, Seq()).foreach(e => {
          val emitter = EndPointEmitter(e, ordering, ListBuffer(), references)
          e.parent match {
            case Some(parent) =>
              all(parent) += emitter
              all += (e -> emitter)
            case _ => all += (e -> emitter)
          }
        })
        defaultOrder(
          all
            .filterKeys(_.parent.isEmpty)
            .values
            .toSeq)

      } else {
        endpoints.map(EndPointEmitter(_, ordering, ListBuffer(), references))
      }

    }

    private case class BaseUriEmitter(fs: Fields) extends EntryEmitter {
      override def emit(b: EntryBuilder): Unit = {
        val protocol: String = fs
          .entry(WebApiModel.Schemes)
          .find(_.value.annotations.contains(classOf[SynthesizedField]))
          .flatMap(_.array.scalars.headOption)
          .map(_.toString)
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

        if (uri.nonEmpty) b.entry("baseUri", uri.url())
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

  case class EndPointEmitter(endpoint: EndPoint,
                             ordering: SpecOrdering,
                             children: mutable.ListBuffer[EndPointEmitter] = mutable.ListBuffer(),
                             references: Seq[BaseUnit] = Seq())
      extends EntryEmitter {

    override def emit(b: EntryBuilder): Unit = {
      val fs = endpoint.fields

      sourceOr(
        endpoint.annotations,
        b.complexEntry(
          b => {
            endpoint.parent.fold(ScalarEmitter(fs.entry(EndPointModel.Path).get.scalar).emit(b))(_ =>
              ScalarEmitter(AmfScalar(endpoint.relativePath)).emit(b))
          },
          _.map { b =>
            val result = mutable.ListBuffer[EntryEmitter]()

            fs.entry(EndPointModel.Name).map(f => result += ValueEmitter("displayName", f))

            fs.entry(EndPointModel.Description).map(f => result += ValueEmitter("description", f))

            fs.entry(EndPointModel.UriParameters)
              .map(f => result += RamlParametersEmitter("uriParameters", f, ordering, references))

            fs.entry(DomainElementModel.Extends).map(f => result ++= ExtendsEmitter("", f, ordering).emitters())

            fs.entry(EndPointModel.Operations).map(f => result ++= operations(f, ordering))

            fs.entry(EndPointModel.Security)
              .map(f => result += ParametrizedSecuritiesSchemeEmitter("securedBy", f, ordering))

            result ++= AnnotationsEmitter(endpoint, ordering).emitters

            result ++= children

            traverse(ordering.sorted(result), b)
          }
        )
      )
    }

    def +=(child: EndPointEmitter): Unit = children += child

    private def operations(f: FieldEntry, ordering: SpecOrdering): Seq[EntryEmitter] = {
      f.array.values
        .map(e => OperationEmitter(e.asInstanceOf[Operation], ordering, references))
    }

    override def position(): Position = pos(endpoint.annotations)
  }

  case class OperationEmitter(operation: Operation, ordering: SpecOrdering, references: Seq[BaseUnit])
      extends EntryEmitter {
    override def emit(b: EntryBuilder): Unit = {
      val fs = operation.fields
      sourceOr(
        operation.annotations,
        b.complexEntry(
          ScalarEmitter(fs.entry(OperationModel.Method).get.scalar).emit(_),
          _.map { b =>
            val result = mutable.ListBuffer[EntryEmitter]()

            fs.entry(OperationModel.Name).map(f => result += ValueEmitter("displayName", f))

            fs.entry(OperationModel.Description).map(f => result += ValueEmitter("description", f))

            fs.entry(OperationModel.Deprecated).map(f => result += ValueEmitter("(deprecated)", f))

            fs.entry(OperationModel.Summary).map(f => result += ValueEmitter("(summary)", f))

            fs.entry(OperationModel.Documentation)
              .map(
                f =>
                  result += OasEntryCreativeWorkEmitter("(externalDocs)",
                                                        f.value.value.asInstanceOf[CreativeWork],
                                                        ordering))

            fs.entry(OperationModel.Schemes).map(f => result += ArrayEmitter("protocols", f, ordering))

            fs.entry(OperationModel.Accepts).map(f => result += ArrayEmitter("(consumes)", f, ordering))

            fs.entry(OperationModel.ContentType).map(f => result += ArrayEmitter("(produces)", f, ordering))

            fs.entry(DomainElementModel.Extends).map(f => result ++= ExtendsEmitter("", f, ordering).emitters())

            result ++= AnnotationsEmitter(operation, ordering).emitters

            Option(operation.request).foreach { req =>
              val fields = req.fields

              fields
                .entry(RequestModel.QueryParameters)
                .map(f => result += RamlParametersEmitter("queryParameters", f, ordering, references))

              fields
                .entry(RequestModel.Headers)
                .map(f => result += RamlParametersEmitter("headers", f, ordering, references))
              fields.entry(RequestModel.Payloads).map(f => result += RamlPayloadsEmitter("body", f, ordering))
            }

            fs.entry(OperationModel.Responses)
              .map(f => result += RamlResponsesEmitter("responses", f, ordering, references))

            fs.entry(OperationModel.Security)
              .map(f => result += ParametrizedSecuritiesSchemeEmitter("securedBy", f, ordering))

            traverse(ordering.sorted(result), b)
          }
        )
      )
    }

    override def position(): Position = pos(operation.annotations)
  }

  case class LicenseEmitter(key: String, f: FieldEntry, ordering: SpecOrdering) extends EntryEmitter {
    override def emit(b: EntryBuilder): Unit = {
      sourceOr(
        f.value,
        b.entry(
          key,
          _.map { b =>
            val fs     = f.obj.fields
            val result = mutable.ListBuffer[EntryEmitter]()

            fs.entry(LicenseModel.Url).map(f => result += ValueEmitter("url", f))
            fs.entry(LicenseModel.Name).map(f => result += ValueEmitter("name", f))

            result ++= AnnotationsEmitter(f.domainElement, ordering).emitters

            traverse(ordering.sorted(result), b)
          }
        )
      )
    }

    override def position(): Position = pos(f.value.annotations)
  }

  case class OrganizationEmitter(key: String, f: FieldEntry, ordering: SpecOrdering) extends EntryEmitter {
    override def emit(b: EntryBuilder): Unit = {
      sourceOr(
        f.value,
        b.entry(
          key,
          _.map { b =>
            val fs     = f.obj.fields
            val result = mutable.ListBuffer[EntryEmitter]()

            fs.entry(OrganizationModel.Url).map(f => result += ValueEmitter("url", f))
            fs.entry(OrganizationModel.Name).map(f => result += ValueEmitter("name", f))
            fs.entry(OrganizationModel.Email).map(f => result += ValueEmitter("email", f))

            result ++= AnnotationsEmitter(f.domainElement, ordering).emitters

            traverse(ordering.sorted(result), b)
          }
        )
      )
    }

    override def position(): Position = pos(f.value.annotations)
  }

}

trait RamlSpecEmitter extends BaseSpecEmitter {

  case class ReferencesEmitter(references: Seq[BaseUnit], ordering: SpecOrdering) extends EntryEmitter {
    override def emit(b: EntryBuilder): Unit = {
      val modules = references.collect({ case m: Module => m })
      if (modules.nonEmpty) {
        b.entry("uses", _.map { b =>
          idCounter.reset()
          traverse(ordering.sorted(modules.map(r => ReferenceEmitter(r, ordering, () => idCounter.genId("uses")))), b)
        })
      }
    }

    override def position(): Position = ZERO
  }

  case class ReferenceEmitter(reference: BaseUnit, ordering: SpecOrdering, aliasGenerator: () => String)
      extends EntryEmitter {

    // todo review with PEdro. We dont serialize location, so when parse amf to dump spec, we lose de location (we only have the id)
    override def emit(b: EntryBuilder): Unit = {
      val alias = reference.annotations.find(classOf[Aliases])

      def entry(alias: String) = MapEntryEmitter(alias, name).emit(b)

      alias.fold {
        entry(aliasGenerator())
      } { _ =>
        alias.foreach(_.aliases.foreach(entry))
      }
    }

    private def name: String = {
      Option(reference.location) match {
        case Some(location) => location
        case None           => reference.id
      }
    }

    override def position(): Position = ZERO
  }

  case class DeclarationsEmitter(declares: Seq[DomainElement], references: Seq[BaseUnit], ordering: SpecOrdering) {
    val emitters: Seq[EntryEmitter] = {
      val declarations = Declarations(declares)

      val result = ListBuffer[EntryEmitter]()

      if (declarations.shapes.nonEmpty)
        result += DeclaredTypesEmitters(declarations.shapes.values.toSeq, references, ordering)

      if (declarations.annotations.nonEmpty)
        result += AnnotationsTypesEmitter(declarations.annotations.values.toSeq, references, ordering)

      if (declarations.resourceTypes.nonEmpty)
        result += AbstractDeclarationsEmitter("resourceTypes",
                                              declarations.resourceTypes.values.toSeq,
                                              ordering,
                                              references)

      if (declarations.traits.nonEmpty)
        result += AbstractDeclarationsEmitter("traits", declarations.traits.values.toSeq, ordering, references)

      if (declarations.securitySchemes.nonEmpty)
        result += RamlSecuritySchemesEmitters(declarations.securitySchemes.values.toSeq, references, ordering)
      if (declarations.parameters.nonEmpty)
        result += DeclaredParametersEmitter(declarations.parameters.values.toSeq, ordering, references)

      result
    }
  }

  case class DeclaredTypesEmitters(types: Seq[Shape], references: Seq[BaseUnit], ordering: SpecOrdering)
      extends EntryEmitter {
    override def emit(b: EntryBuilder): Unit = {
      b.entry("types", _.map { b =>
        traverse(ordering.sorted(types.map(s => NamedTypeEmitter(s, references, ordering))), b)
      })
    }

    override def position(): Position = types.headOption.map(a => pos(a.annotations)).getOrElse(ZERO)
  }

  case class DeclaredParametersEmitter(parameters: Seq[Parameter], ordering: SpecOrdering, references: Seq[BaseUnit])
      extends EntryEmitter {
    override def emit(b: EntryBuilder): Unit = {
      b.entry(
        "(parameters)",
        _.map(traverse(ordering.sorted(parameters.map(NamedParameterEmitter(_, ordering, references))), _))
      )
    }

    override def position(): Position = parameters.headOption.map(a => pos(a.annotations)).getOrElse(ZERO)
  }

  case class NamedTypeEmitter(shape: Shape, references: Seq[BaseUnit], ordering: SpecOrdering) extends EntryEmitter {
    override def emit(b: EntryBuilder): Unit = {
      val name = Option(shape.name).getOrElse(throw new Exception(s"Cannot declare shape without name $shape"))
      b.entry(name, if (shape.isLink) emitLink _ else emitInline _)
    }

    private def emitLink(b: PartBuilder): Unit = {
      shape.linkTarget.foreach { l =>
        spec.tagToReference(l, shape.linkLabel, references).emit(b)
      }
    }

    private def emitInline(b: PartBuilder): Unit = TypePartEmitter(shape, ordering, None).emit(b)

    override def position(): Position = pos(shape.annotations)
  }

  case class NamedParameterEmitter(parameter: Parameter, ordering: SpecOrdering, references: Seq[BaseUnit])
      extends EntryEmitter {
    override def emit(b: EntryBuilder): Unit = {
      parameter.fields.get(ParameterModel.Binding).annotations += ExplicitField()
      RamlParameterEmitter(parameter, ordering, references).emit(b)
    }

    override def position(): Position = pos(parameter.annotations)
  }

  case class AnnotationsTypesEmitter(properties: Seq[CustomDomainProperty],
                                     references: Seq[BaseUnit],
                                     ordering: SpecOrdering)
      extends EntryEmitter {
    override def emit(b: EntryBuilder): Unit = {
      b.entry("annotationTypes", _.map { b =>
        traverse(ordering.sorted(properties.map(p => NamedPropertyTypeEmitter(p, references, ordering))), b)
      })
    }
    override def position(): Position = properties.headOption.map(p => pos(p.annotations)).getOrElse(ZERO)
  }

  case class NamedPropertyTypeEmitter(annotation: CustomDomainProperty,
                                      references: Seq[BaseUnit],
                                      ordering: SpecOrdering)
      extends EntryEmitter {

    override def emit(b: EntryBuilder): Unit = {
      val name = Option(annotation.name).orElse(throw new Exception(s"Annotation type without name $annotation")).get
      b.entry(name, if (annotation.isLink) emitLink _ else emitInline _)
    }

    private def emitLink(b: PartBuilder): Unit = {
      annotation.linkTarget.foreach { l =>
        spec.tagToReference(l, annotation.linkLabel, references).emit(b)
      }
    }

    private def emitInline(b: PartBuilder): Unit = {
      b.map { b =>
        traverse(ordering.sorted(AnnotationTypeEmitter(annotation, ordering).emitters()), b)
      }
    }

    override def position(): Position = pos(annotation.annotations)
  }

  case class UserDocumentationsEmitter(f: FieldEntry, ordering: SpecOrdering) extends EntryEmitter {
    override def emit(b: EntryBuilder): Unit = {
      b.entry(
        "documentation",
        _.list { b =>
          f.array.values
            .collect({ case c: CreativeWork => c })
            .foreach(
              c =>
                if (c.isLink)
                  raw(b, c.linkLabel.getOrElse(c.linkTarget.get.id))
                else
                  RamlCreativeWorkEmitter(c, ordering, withExtension = true).emit(b))
        }
      )
    }

    override def position(): Position = pos(f.array.values.head.annotations)
  }

  case class OasExtCreativeWorkEmitter(f: FieldEntry, ordering: SpecOrdering) extends EntryEmitter {
    override def emit(b: EntryBuilder): Unit = {
      sourceOr(
        f.value.annotations,
        b.entry(
          "(externalDocs)",
          OasCreativeWorkEmitter(f.value.value.asInstanceOf[CreativeWork], ordering).emit(_)
        )
      )
    }

    override def position(): Position = pos(f.value.annotations)
  }

  override implicit val spec: SpecEmitterContext = RamlSpecEmitterContext
}

object RamlSpecEmitterContext extends SpecEmitterContext {
  override def ref(b: PartBuilder, url: String): Unit = b.scalar(YNode(YScalar("!include " + url), YType("!include")))

  override val vendor: Vendor = Raml

  override def localReference(reference: Linkable): PartEmitter = RamlLocalReferenceEmitter(reference)
}
