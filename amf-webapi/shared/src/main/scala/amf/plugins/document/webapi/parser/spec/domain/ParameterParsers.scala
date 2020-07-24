package amf.plugins.document.webapi.parser.spec.domain

import amf.core.annotations.{AutoGeneratedName, ExplicitField, LexicalInformation, SynthesizedField}
import amf.core.metamodel.domain.ShapeModel
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.model.domain.{AmfArray, AmfScalar, DomainElement, NamedDomainElement, Shape}
import amf.core.parser.{Annotations, _}
import amf.core.utils.{AmfStrings, IdCounter}
import amf.core.validation.core.ValidationSpecification
import amf.plugins.document.webapi.annotations.{
  BodyParameter,
  FormBodyParameter,
  Inferred,
  ParameterNameForPayload,
  RequiredParamPayload
}
import amf.plugins.document.webapi.contexts.WebApiContext
import amf.plugins.document.webapi.contexts.parser.raml.RamlWebApiContext
import amf.plugins.document.webapi.contexts.parser.oas.OasWebApiContext
import amf.plugins.document.webapi.parser.spec.WebApiDeclarations.ErrorParameter
import amf.plugins.document.webapi.parser.spec.common.{AnnotationParser, SpecParserOps}
import amf.plugins.document.webapi.parser.spec.declaration.{
  OasTypeParser,
  Raml08TypeParser,
  Raml10TypeParser,
  RamlTypeSyntax,
  StringDefaultType,
  _
}
import amf.plugins.document.webapi.parser.spec.raml.RamlTypeExpressionParser
import amf.plugins.document.webapi.parser.spec.{OasDefinitions, toOas}
import amf.plugins.domain.shapes.models.ExampleTracking._
import amf.plugins.domain.shapes.models.{AnyShape, Example, FileShape, NodeShape}
import amf.plugins.domain.webapi.annotations.{InvalidBinding, ParameterBindingInBodyLexicalInfo}
import amf.plugins.domain.webapi.metamodel.{ParameterModel, PayloadModel, ResponseModel}
import amf.plugins.domain.webapi.models.{Parameter, Payload, SchemaContainer}
import amf.plugins.features.validation.CoreValidations.UnresolvedReference
import amf.validations.ParserSideValidations
import amf.validations.ParserSideValidations._
import org.yaml.model.{YMap, YMapEntry, YScalar, YType, _}

import scala.language.postfixOps
case class RamlParametersParser(map: YMap, adopted: Parameter => Unit, parseOptional: Boolean = false)(
    implicit ctx: RamlWebApiContext) {

  def parse(): Seq[Parameter] =
    map.entries
      .map(entry => ctx.factory.parameterParser(entry, adopted, parseOptional).parse())
}

object RamlHeaderParser {
  def parse(adopted: Parameter => Unit, parseOptional: Boolean = false)(node: YNode)(
      implicit ctx: RamlWebApiContext): Parameter = {
    RamlParameterParser.parse(adopted, parseOptional)(node).withBinding("header")
  }
}

object RamlQueryParameterParser {
  def parse(adopted: Parameter => Unit, parseOptional: Boolean = false)(node: YNode)(
      implicit ctx: RamlWebApiContext): Parameter = {
    RamlParameterParser.parse(adopted, parseOptional)(node).withBinding("query")
  }
}

object RamlParameterParser {
  def parse(adopted: Parameter => Unit, parseOptional: Boolean = false)(node: YNode)(
      implicit ctx: RamlWebApiContext): Parameter = {
    val head = node.as[YMap].entries.head
    ctx.factory.parameterParser(head, adopted, parseOptional).parse()
  }
}

case class Raml10ParameterParser(entry: YMapEntry, adopted: Parameter => Unit, parseOptional: Boolean = false)(
    implicit ctx: RamlWebApiContext)
    extends RamlParameterParser(entry, adopted) {
  override def parse(): Parameter = {

    val name      = ScalarNode(entry.key)
    val parameter = Parameter(entry).set(ParameterModel.Name, name.text()).withParameterName(name.text().toString) // TODO parameter id is using a name that is not final.
    adopted(parameter)

    val p = entry.value.to[YMap] match {
      case Right(map) =>
        map.key("required", (ParameterModel.Required in parameter).explicit.allowingAnnotations)
        map.key("description", (ParameterModel.Description in parameter).allowingAnnotations)
        map.key("binding".asRamlAnnotation, (ParameterModel.Binding in parameter).explicit)

        Raml10TypeParser(entry,
                         shape => shape.withName("schema").adopted(parameter.id),
                         TypeInfo(isPropertyOrParameter = true))
          .parse()
          .foreach(s => parameter.set(ParameterModel.Schema, tracking(s, parameter.id), Annotations(entry)))

        AnnotationParser(parameter, map).parse()

        parameter
      case _ =>
        val scope = ctx.link(entry.value) match {
          case Left(_) => SearchScope.Fragments
          case _       => SearchScope.Named
        }
        entry.value.tagType match {
          case YType.Null =>
            Raml10TypeParser(
              entry,
              shape => shape.withName("schema").adopted(parameter.id)
            ).parse().foreach { schema =>
              tracking(schema, parameter.id).annotations += SynthesizedField()
              parameter.set(ParameterModel.Schema, schema, Annotations(entry))
            }
            parameter
          case _ => // we have a property type
            entry.value.to[YScalar] match {
              case Right(ref) if ctx.declarations.findParameter(ref.text, scope).isDefined =>
                ctx.declarations
                  .findParameter(ref.text, scope)
                  .get
                  .link(ref.text, Annotations(entry))
                  .asInstanceOf[Parameter]
                  .set(ParameterModel.Name, name.text())
              case Right(ref) if ctx.declarations.findType(ref.text, scope).isDefined =>
                val shape: Shape = ctx.declarations
                  .findType(ref.text,
                            scope,
                            Some((s: String) => ctx.eh.violation(InvalidFragmentType, parameter.id, s, entry.value)))
                  .get
                  .link[Shape](ref.text, Annotations(entry))
                parameter.withSchema(shape.withName("schema").adopted(parameter.id))
              case Right(ref) if wellKnownType(ref.text, isRef = true) =>
                val shape: Shape = parseWellKnownTypeRef(ref.text)
                val schema       = shape.withName("schema").adopted(parameter.id)
                parameter.withSchema(schema)

              case Right(ref) if isTypeExpression(ref.text) =>
                RamlTypeExpressionParser(shape => shape.withName("schema").adopted(parameter.id),
                                         expression = ref.text)
                  .parse() match {
                  case Some(schema) => parameter.withSchema(schema)
                  case _ =>
                    ctx.eh.violation(UnresolvedReference,
                                     parameter.id,
                                     s"Cannot parse type expression for unresolved parameter '${parameter.name}'",
                                     entry.value)
                    parameter
                }
              case _ =>
                ctx.eh.violation(UnresolvedReference, parameter.id, "Cannot declare unresolved parameter", entry.value)
                parameter

            }
        }
    }

    if (p.fields.entry(ParameterModel.Required).isEmpty) {
      val stringName = name.text().toString
      val required   = !stringName.endsWith("?")
      val paramName  = if (required) stringName else stringName.stripSuffix("?")
      p.set(ParameterModel.Required, required)
      p.set(ParameterModel.Name, AmfScalar(paramName, name.text().annotations))
        .set(ParameterModel.ParameterName, paramName)
    }

    p
  }
}

case class Raml08ParameterParser(entry: YMapEntry, adopted: Parameter => Unit, parseOptional: Boolean = false)(
    implicit ctx: RamlWebApiContext)
    extends RamlParameterParser(entry, adopted) {
  def parse(): Parameter = {

    val name      = ScalarNode(entry.key)
    val parameter = Parameter(entry).set(ParameterModel.Name, name.text()).withParameterName(name.text().toString)
    adopted(parameter)

    entry.value.tagType match {
      case YType.Null =>
        Raml10TypeParser(
          entry,
          shape => shape.withName("schema").adopted(parameter.id)
        ).parse().foreach { schema =>
          tracking(schema, parameter.id).annotations += SynthesizedField()
          parameter.set(ParameterModel.Schema, schema, Annotations(entry))
        }
      case _ =>
        // Named Parameter Parse
        Raml08TypeParser(entry,
                         (s: Shape) => s.withName(name.text().toString).adopted(parameter.id),
                         isAnnotation = false,
                         StringDefaultType)
          .parse()
          .foreach(s => parameter.withSchema(tracking(s, parameter.id)))
    }

    entry.value.toOption[YMap] match {
      case Some(map) =>
        map.key("required", (ParameterModel.Required in parameter).explicit)
        map.key("description", (ParameterModel.Description in parameter).allowingAnnotations)
      case _ =>
    }

    if (parameter.fields.entry(ParameterModel.Required).isEmpty) parameter.set(ParameterModel.Required, value = false)

    val stringName = name.text().toString
    if (parseOptional && stringName.endsWith("?")) {
      parameter.set(ParameterModel.Optional, value = true)
      val n = stringName.stripSuffix("?")
      parameter.set(ParameterModel.Name, AmfScalar(n, name.text().annotations)).set(ParameterModel.ParameterName, n)
    }

    parameter
  }
}

abstract class RamlParameterParser(entry: YMapEntry, adopted: Parameter => Unit)(implicit val ctx: RamlWebApiContext)
    extends RamlTypeSyntax
    with SpecParserOps {
  def parse(): Parameter
}

trait OasParameterParser extends SpecParserOps {
  def parse: OasParameter
}

case class Oas2ParameterParser(entryOrNode: Either[YMapEntry, YNode],
                               parentId: String,
                               nameNode: Option[YNode],
                               nameGenerator: IdCounter)(implicit ctx: WebApiContext)
    extends OasParameterParser {

  protected val map: YMap = entryOrNode match {
    case Left(entry) => entry.value.as[YMap]
    case Right(node) => node.as[YMap]
  }

  protected def setName(p: DomainElement with NamedDomainElement): DomainElement = {
    p match {
      case _: Shape if nameNode.isDefined =>
        p.set(ShapeModel.Name, nameNode.map(ScalarNode(_).text()).get)
      case _: Shape =>
        map.key("name", (ShapeModel.Name in p).withAnnotation(Inferred())) // name of the parameter in the HTTP binding (path, request parameter, etc)
      case p: Payload =>
        if (nameNode.nonEmpty)
          p.set(
            ParameterModel.Name,
            nameNode.map(ScalarNode(_).text()).get,
            map
              .key("name")
              .map(e => {
                Annotations(ParameterNameForPayload(ScalarNode(e.value).text().value.toString, Range(e.range)))
              })
              .getOrElse(Annotations())
          )
        else map.key("name", ParameterModel.Name in p)
        validateEntryName(p)
      case _ =>
        if (nameNode.nonEmpty)
          p.set(ParameterModel.Name, nameNode.map(ScalarNode(_).text()).get)
        else
          map.key("name", ParameterModel.Name in p) // name of the parameter in the HTTP binding (path, request parameter, etc)
        validateEntryName(p)
    }
    p
  }

  private def buildFromBinding(in: String, bindingEntry: Option[YMapEntry]): OasParameter = {
    in match {
      case "body" =>
        OasParameter(parseBodyPayload(bindingEntry.map(a => Range(a.range))),
                     entryOrNode.toOption.orElse(entryOrNode.left.toOption))
      case "formData" =>
        OasParameter(parseFormDataPayload(bindingEntry.map(a => Range(a.range))),
                     entryOrNode.toOption.orElse(entryOrNode.left.toOption))
      case "query" | "header" | "path" =>
        OasParameter(parseCommonParam(in), entryOrNode.toOption.orElse(entryOrNode.left.toOption))
      case _ =>
        val oasParam = buildFromBinding(defaultBinding, None)
        invalidBinding(bindingEntry, in, oasParam)
        oasParam
    }
  }

  private def validateEntryName(element: DomainElement with NamedDomainElement): Unit = {
    // if is invalid need a aut generated name different from default to avoid collision with uri parameters
    if (element.name.option().isEmpty)
      element.withName(nameGenerator.genId("parameter"), Annotations() += AutoGeneratedName())
    element.adopted(parentId)
    if (map.key("name").isEmpty) {
      ctx.eh.violation(
        ParameterNameRequired,
        element.id,
        "'name' property is required in a parameter.",
        map
      )
    }
  }

  def parse(): OasParameter = {
    map.key("$ref") match {
      case Some(ref) => parseParameterRef(ref, parentId)
      case None =>
        map.key("in") match {
          case Some(entry: YMapEntry) =>
            val in = entry.value.as[YScalar].text
            buildFromBinding(in, Some(entry))
          case _ => // ignore
            /**
              * Binding is required, i'm not setting any default value so It will be some model validation.
              * */
            val parameter = Parameter(map)
            setName(parameter)
            parameter.adopted(parentId)
            OasParameter(parameter, Some(map))
        }
    }
  }

  protected def parseCommonParam(binding: String): Parameter = {
    val parameter = parseFixedFields()
    parseType(
      parameter,
      binding,
      () => {
        OasTypeParser(
          entryOrNode,
          "schema",
          map,
          shape => shape.withName("schema").adopted(parameter.id),
          OAS20SchemaVersion(position = "parameter")(ctx.eh)
        )(toOas(ctx))
          .parse()
          .map { schema =>
            parameter.set(ParameterModel.Schema, schema, Annotations(map))
          }
          .orElse {
            ctx.eh.violation(
              UnresolvedParameter,
              parameter.id,
              "Cannot find valid schema for parameter",
              map
            )
            None
          }
      }
    )
    parameter
  }

  protected def parseFixedFields(): Parameter = {
    val parameter = Parameter(entryOrNode.toOption.getOrElse(entryOrNode.left.get))
    setName(parameter)
    parameter.adopted(parentId)
    parameter.set(ParameterModel.Required, value = false)

    map.key("name", ParameterModel.ParameterName in parameter) // name of the parameter in the HTTP binding (path, request parameter, etc)

    map.key("in", ParameterModel.Binding in parameter)
    map.key("description", ParameterModel.Description in parameter)
    map.key("required", (ParameterModel.Required in parameter).explicit)

    ctx.closedShape(parameter.id, map, "parameter")
    AnnotationParser(parameter, map).parse()
    parameter
  }

  def parseType(container: SchemaContainer, binding: String, typeParsing: () => Unit) = {

    def setDefaultSchema = (c: SchemaContainer) => c.setSchema(AnyShape(Annotations(Inferred())))

    map.key("type") match {
      case None =>
        setDefaultSchema(container)
        ctx.eh.violation(MissingParameterType,
                         container.id,
                         s"'type' is required in a parameter with binding '${binding}'",
                         map)
      case Some(_) => typeParsing()
    }
    container
  }

  private def parseFormDataPayload(bindingRange: Option[Range]): Payload = {
    val payload = commonPayload(bindingRange)
    ctx.closedShape(payload.id, map, "parameter")
    parseType(
      payload,
      "formData",
      () => {
        new OasTypeParser(
          entryOrNode,
          "schema",
          map,
          shape => setName(shape).asInstanceOf[Shape].adopted(payload.id),
          OAS20SchemaVersion(position = "parameter")(ctx.eh)
        )(toOas(ctx))
          .parse()
          .map { schema =>
            payload.set(PayloadModel.Schema, tracking(schema, payload.id), Annotations(map))
          }
          .orElse {
            ctx.eh.violation(
              UnresolvedParameter,
              payload.id,
              "Cannot find valid schema for parameter",
              map
            )
            None
          }
      }
    )
    payload.annotations += FormBodyParameter()
    payload
  }

  private def commonPayload(bindingRange: Option[Range]): Payload = {
    val payload = Payload(entryOrNode.toOption.getOrElse(entryOrNode.left.get))
    setName(payload)
    if (payload.name.option().isEmpty)
      payload.set(ParameterModel.Name, AmfScalar("default"), Annotations() += Inferred())
    map.key("required", entry => {
      val req: Boolean = entry.value.as[Boolean]
      payload.annotations += RequiredParamPayload(req, Range(entry.range))
    })
    map.key("description", PayloadModel.Description in payload)
    AnnotationParser(payload, map).parse()
    payload
  }

  private def parseBodyPayload(bindingRange: Option[Range]): Payload = {
    val payload: Payload = commonPayload(bindingRange)
    ctx.closedShape(payload.id, map, "bodyParameter")

    map.key("mediaType".asOasExtension, PayloadModel.MediaType in payload)
    // Force to re-adopt with the new mediatype if exists
    if (payload.mediaType.nonEmpty) validateEntryName(payload)

    map.key(
      "schema",
      entry => {
        OasTypeParser(entry, shape => setName(shape).asInstanceOf[Shape].adopted(payload.id))(toOas(ctx)) // i don't need to set param need in here. Its necesary only for form data, because of the properties
          .parse()
          .map { schema =>
            checkNotFileInBody(schema)
            payload.set(PayloadModel.Schema, tracking(schema, payload.id), Annotations(entry))
            bindingRange.foreach { range =>
              schema.annotations += ParameterBindingInBodyLexicalInfo(range)
            }
            schema
          }
      }
    )

    payload
  }

  private def invalidBinding(bindingEntry: Option[YMapEntry], binding: String, parameter: OasParameter): Unit = {
    val entryValueAnnotations = bindingEntry.map(e => Annotations(e.value)).getOrElse(Annotations())

    ctx.eh.violation(OasInvalidParameterBinding,
                     "",
                     s"Invalid parameter binding '$binding'",
                     bindingEntry.map(_.value).getOrElse(map))

    parameter.domainElement match {
      case p: Parameter =>
        p.set(ParameterModel.Binding,
              AmfScalar(p.binding.value(), entryValueAnnotations),
              entryValueAnnotations += InvalidBinding(binding))
      case p: Payload =>
        p.add(InvalidBinding(binding))
      case _ => // ignore
    }
  }

  def defaultBinding: String = map.key("schema").map(_ => "body").getOrElse("query")

  protected def checkNotFileInBody(schema: Shape): Unit = {
    val schemaToCheck =
      if (schema.isLink) schema.linkTarget
      else schema
    if (schemaToCheck.isInstanceOf[FileShape])
      ctx.eh.violation(
        OasFormDataNotFileSpecification,
        schema.id,
        "File types in parameters must be declared in formData params",
        map
      )
  }

  protected def parseParameterRef(ref: YMapEntry, parentId: String): OasParameter = {
    val refUrl = OasDefinitions.stripParameterDefinitionsPrefix(ref.value)
    ctx.declarations.findParameter(refUrl, SearchScope.All) match {
      case Some(param) =>
        val parameter: Parameter = param.link(refUrl, Annotations(map))
        parameter.withName(refUrl).adopted(parentId)
        OasParameter(parameter, Some(ref))
      case None =>
        ctx.declarations.findPayload(refUrl, SearchScope.All) match {
          case Some(payload) =>
            OasParameter(payload.link(refUrl, Annotations(map)).asInstanceOf[Payload], Some(ref))
          case None =>
            val fullRef = ctx.resolvedPath(ctx.rootContextDocument, refUrl)
            ctx.parseRemoteOasParameter(fullRef, parentId)(toOas(ctx)) match {
              case Some(oasParameter) =>
                for {
                  param <- oasParameter.parameter
                  name  <- nameNode.map(ScalarNode(_).text())
                } yield {
                  param.set(ParameterModel.Name, name).adopted(parentId)
                }
                oasParameter
              case _ =>
                val parameter: Parameter = ErrorParameter(refUrl, ref).link(refUrl, Annotations(ref))
                setName(parameter)
                parameter.adopted(parentId)
                ctx.eh.violation(UnresolvedParameter,
                                 parameter.id,
                                 s"Cannot find parameter or payload reference $refUrl",
                                 ref)
                OasParameter(parameter, Some(ref))
            }
        }
    }
  }
}

class Oas3ParameterParser(entryOrNode: Either[YMapEntry, YNode],
                          parentId: String,
                          nameNode: Option[YNode],
                          nameGenerator: IdCounter)(implicit ctx: WebApiContext)
    extends Oas2ParameterParser(entryOrNode, parentId, nameNode, nameGenerator) {

  override def parse(): OasParameter = {
    map.key("$ref") match {
      case Some(ref) => parseParameterRef(ref, parentId)
      case None      => buildParameter()
    }
  }

  def buildParameter(): OasParameter = {
    val result = parseFixedFields()
    parseSchema(result)
    parseExamples(result)
    parseContent(result)
    parseQueryFields(result)
    Oas3ParameterParser.parseStyleField(map, result)
    Oas3ParameterParser.parseExplodeField(map, result)
    map.key("deprecated", ParameterModel.Deprecated in result)
    Oas3ParameterParser.validateSchemaOrContent(map, result)
    OasParameter(result)
  }

  private def parseQueryFields(result: Parameter): Unit = {
    result.binding
      .option()
      .foreach(b =>
        if (b == "query") {
          result.set(ParameterModel.AllowReserved, value = false)
          result.set(ParameterModel.AllowEmptyValue, value = false)
          map.key("allowReserved", (ParameterModel.AllowReserved in result).explicit)
          map.key("allowEmptyValue", (ParameterModel.AllowEmptyValue in result).explicit)
      })
  }

  private def parseSchema(param: Parameter): Unit =
    map.key(
      "schema",
      entry => {
        OasTypeParser(entry,
                      shape => setName(shape).asInstanceOf[Shape].adopted(param.id),
                      OAS30SchemaVersion("schema")(ctx.eh))(toOas(ctx))
          .parse()
          .map { schema =>
            ctx.autoGeneratedAnnotation(schema)
            param.set(ParameterModel.Schema, schema, Annotations(entry))
          }
      }
    )

  private def parseExamples(param: Parameter): Unit = {
    def setShape(examples: Seq[Example], maybeEntry: Option[YMapEntry]): Unit =
      maybeEntry
        .map(entry => param.set(PayloadModel.Examples, AmfArray(examples), Annotations(entry)))
        .getOrElse(param.set(PayloadModel.Examples, AmfArray(examples)))

    OasExamplesParser(map, param).parse()
  }

  private def parseContent(param: Parameter): Unit = {
    val payloadProducer: Option[String] => Payload = mediaType => {
      val res = Payload()
      mediaType.map(res.withMediaType)
      res.adopted(param.id)
    }
    map.key(
      "content",
      entry => {
        val payloads = OasContentsParser(entry, payloadProducer)(toOas(ctx)).parse()
        if (payloads.nonEmpty) param.set(ResponseModel.Payloads, AmfArray(payloads), Annotations(entry))
      }
    )
  }

}

object Oas3ParameterParser {

  lazy val validStyles: Map[String, List[String]] = Map(
    "matrix"         -> List("path"),
    "label"          -> List("path"),
    "form"           -> List("query", "cookie"),
    "simple"         -> List("path", "header"),
    "spaceDelimited" -> List("query"),
    "pipeDelimited"  -> List("query"),
    "deepObject"     -> List("query")
  )

  def parseExplodeField(map: YMap, result: Parameter): Unit = {
    map.key("explode") match {
      case Some(entry) =>
        result.fields.setWithoutId(ParameterModel.Explode,
                                   AmfScalar(entry.value.as[Boolean]),
                                   Annotations(entry) += ExplicitField())
      case None =>
        val defValue: Option[Boolean] = result.style.option().map {
          case "form" => true
          case _      => false
        }
        defValue.foreach(result.set(ParameterModel.Explode, _))
    }
  }

  def parseStyleField(map: YMap, result: Parameter)(implicit ctx: WebApiContext): Unit = {
    map.key("style") match {
      case Some(entry) =>
        val styleText = entry.value.asScalar.map(_.text).getOrElse("")
        validateStyle(result, styleText, ctx)
        result.fields.setWithoutId(ParameterModel.Style,
                                   AmfScalar(styleText, Annotations(entry.value)),
                                   Annotations(entry) += ExplicitField())
      case None =>
        val defValue: Option[String] = result.binding.option() match {
          case Some("query") | Some("cookie") => Some("form")
          case Some("path") | Some("header")  => Some("simple")
          case _                              => None
        }
        defValue.foreach(result.set(ParameterModel.Style, _))
    }
  }

  private def isStyleValid(paramBinding: String, style: String): Boolean = Oas3ParameterParser.validStyles.exists {
    case (key, value) => key.equals(style) && value.contains(paramBinding)
  }

  private def validateStyle(param: Parameter, style: String, ctx: WebApiContext): Unit = {
    val paramBinding = param.binding.value()
    if (!isStyleValid(paramBinding, style))
      ctx.eh.violation(InvalidParameterStyleBindingCombination,
                       param.id,
                       s"'$style' style cannot be used with '$paramBinding' value of parameter property 'in'")
  }

  def validateSchemaOrContent(map: YMap, param: Parameter)(implicit ctx: WebApiContext): Unit = {
    (map.key("schema"), map.key("content")) match {
      case (Some(_), Some(_)) | (None, None) =>
        ctx.eh.violation(
          ParserSideValidations.ParameterMissingSchemaOrContent,
          param.id,
          s"Parameter must define a 'schema' or 'content' field, but not both",
          param.annotations
        )
      case _ =>
    }
  }
}

case class OasParametersParser(values: Seq[YNode], parentId: String)(implicit ctx: OasWebApiContext) {

  def formDataPayload(formData: Seq[Payload]): Option[Payload] =
    if (formData.isEmpty) None
    else {
      val shape: NodeShape = NodeShape()
      val schema           = shape.withName("formData").adopted(parentId)

      formData.foreach { p =>
        val payload = if (p.isLink) p.effectiveLinkTarget().asInstanceOf[Payload] else p

        val property = schema.withProperty(payload.name.value())
        payload.annotations.find(classOf[RequiredParamPayload]) match {
          case None => property.set(PropertyShapeModel.MinCount, 0)
          case Some(a) if !a.required =>
            property.set(PropertyShapeModel.MinCount,
                         AmfScalar(0, Annotations() += LexicalInformation(a.range) += ExplicitField()))
          case Some(a) =>
            property.set(PropertyShapeModel.MinCount,
                         AmfScalar(1, Annotations() += LexicalInformation(a.range) += ExplicitField()))
        }

        Option(payload.schema).foreach(property.withRange(_).adopted(property.id))
      }

      Some(Payload().withName("formData").adopted(parentId).set(PayloadModel.Schema, schema).add(FormBodyParameter()))
    }

  private case class ParameterInformation(oasParam: OasParameter, name: String, binding: String)

  def parse(inRequestOrEndpoint: Boolean = false): Parameters = {
    val nameGenerator = new IdCounter()
    val oasParameters = values
      .map(value => ctx.factory.parameterParser(Right(value), parentId, None, nameGenerator).parse)

    val formData = oasParameters.flatMap(_.formData)
    val body     = oasParameters.filter(_.isBody)
    body.foreach(_.domainElement.annotations += BodyParameter())

    validateDuplicated(oasParameters)

    if (inRequestOrEndpoint) {
      if (body.nonEmpty && formData.nonEmpty) {
        val bodyParam = body.head
        ctx.eh.violation(
          OasBodyAndFormDataParameterSpecification,
          bodyParam.domainElement.id,
          "Cannot declare 'body' and 'formData' params at the same time for a request or resource",
          bodyParam.ast.get
        )
      }

      validateOasPayloads(body, OasInvalidBodyParameter)
    }

    Parameters(
      oasParameters.flatMap(_.query) ++ oasParameters.flatMap(_.invalids),
      oasParameters.flatMap(_.path),
      oasParameters.flatMap(_.header),
      oasParameters.flatMap(_.cookie),
      Nil,
      body.flatMap(_.body) ++ formDataPayload(formData)
    )
  }

  private def validateDuplicated(oasParameters: Seq[OasParameter]): Unit = {
    val paramsInformation = oasParameters.flatMap(
      oasParam => {
        oasParam.element match {
          case Left(parameter) =>
            val effectiveParam = parameter.effectiveLinkTarget().asInstanceOf[Parameter]
            for {
              name    <- Option(effectiveParam.parameterName.value())
              binding <- Option(effectiveParam.binding.value())
            } yield ParameterInformation(oasParam, name, binding)
          case Right(payload) =>
            val effectivePayload = payload.effectiveLinkTarget().asInstanceOf[Payload]
            val name             = obtainName(effectivePayload)
            Some(ParameterInformation(oasParam, name, if (oasParam.isFormData) "formData" else "body"))
        }
      }
    )

    val groupedByBinding = paramsInformation.groupBy {
      case ParameterInformation(_, name, binding) => (name, binding)
    }.values
    groupedByBinding
      .foreach {
        case equalParams if equalParams.length > 1 =>
          equalParams.tail.foreach {
            case ParameterInformation(oasParam, name, binding) =>
              oasParam.ast match {
                case Some(ast) =>
                  ctx.eh.violation(
                    DuplicatedParameters,
                    oasParam.domainElement.id,
                    s"Parameter $name of type $binding was found duplicated",
                    ast
                  )
                case None =>
                  ctx.eh.violation(
                    DuplicatedParameters,
                    oasParam.domainElement.id,
                    s"Parameter $name of type $binding was found duplicated"
                  )
              }

          }
        case _ =>
      }
  }

  private def obtainName(payload: Payload): String =
    payload.fields
      .getValueAsOption(PayloadModel.Name)
      .flatMap(_.annotations.find(classOf[ParameterNameForPayload]).map(_.paramName))
      .getOrElse(payload.name.value())

  private def validateOasPayloads(params: Seq[OasParameter], id: ValidationSpecification): Unit =
    if (params.length > 1) {
      params.tail.foreach { param =>
        ctx.eh.violation(
          id,
          param.domainElement.id,
          "Cannot declare more than one 'body' parameter for a request or a resource",
          param.ast.get
        )
      }
    }
}
