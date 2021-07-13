package amf.apicontract.internal.spec.raml.parser.domain

import amf.apicontract.client.scala.model.domain.{Parameter, Payload, Response}
import amf.apicontract.internal.annotations.EmptyPayload
import amf.apicontract.internal.metamodel.domain.{PayloadModel, RequestModel, ResponseModel}
import amf.apicontract.internal.spec.common.parser.{RamlHeaderParser, SpecParserOps, WebApiShapeParserContextAdapter}
import amf.apicontract.internal.spec.oas.parser.domain.ExamplesByMediaTypeParser
import amf.apicontract.internal.spec.raml.parser.context.RamlWebApiContext
import amf.core.client.scala.model.domain.{AmfArray, AmfScalar}
import amf.core.internal.annotations.SynthesizedField
import amf.core.internal.parser.YMapOps
import amf.core.internal.parser.domain.{Annotations, ScalarNode, SearchScope}
import amf.core.internal.utils.AmfStrings
import amf.core.internal.validation.CoreParserValidations.UnsupportedExampleMediaTypeErrorSpecification
import amf.shapes.client.scala.model.domain.ExampleTracking.tracking
import amf.shapes.internal.spec.common.parser.AnnotationParser
import amf.shapes.internal.spec.raml.parser.{AnyDefaultType, DefaultType}
import amf.shapes.internal.vocabulary.VocabularyMappings
import org.yaml.model.{YMap, YMapEntry, YScalar, YType}

import scala.collection.mutable

/**
  *
  */
case class Raml10ResponseParser(entry: YMapEntry, adopt: Response => Unit, parseOptional: Boolean = false)(
    implicit ctx: RamlWebApiContext)
    extends RamlResponseParser(entry, adopt, parseOptional) {

  override def parseMap(response: Response, map: YMap): Unit = {
    AnnotationParser(response, map, List(VocabularyMappings.response))(WebApiShapeParserContextAdapter(ctx)).parse()
  }

  override def supportsOptionalResponses: Boolean = false

  override protected val defaultType: DefaultType = AnyDefaultType
}

case class Raml08ResponseParser(entry: YMapEntry, adopt: Response => Unit, parseOptional: Boolean = false)(
    implicit ctx: RamlWebApiContext)
    extends RamlResponseParser(entry, adopt, parseOptional) {
  override protected def parseMap(response: Response, map: YMap): Unit = Unit

  override protected val defaultType: DefaultType = AnyDefaultType

  override def supportsOptionalResponses: Boolean = true
}

abstract class RamlResponseParser(entry: YMapEntry, adopt: Response => Unit, parseOptional: Boolean = false)(
    implicit ctx: RamlWebApiContext)
    extends SpecParserOps {

  protected def parseMap(response: Response, map: YMap)

  protected val defaultType: DefaultType

  def supportsOptionalResponses: Boolean

  def parse(): Response = {
    val node = ScalarNode(entry.key)

    val response: Response = entry.value.tagType match {
      case YType.Null =>
        val response =
          Response(entry).withName(node).set(ResponseModel.StatusCode, node.text(), Annotations.inferred())
        adopt(response)
        response
      case YType.Str | YType.Int =>
        val ref = entry.value.as[YScalar].text
        val res: Response = ctx.declarations
          .findResponseOrError(entry.value)(ref, SearchScope.All)
          .link(ref)
        res.withName(node).annotations ++= Annotations(entry)
        res
      case _ =>
        val map = entry.value.as[YMap] // if not scalar, must be the response, if not, violation.
        val res = Response(entry).withName(node)
        adopt(res)
        // res.withStatusCode(if (res.name.value() == "default") "200" else res.name.value())
        val textNode = node.text()
        res.set(ResponseModel.StatusCode, node.text(), Annotations.inferred())

        if (parseOptional && textNode.toString.endsWith("?") && supportsOptionalResponses) {
          res.set(ResponseModel.Optional, value = true)
          val name = textNode.toString.stripSuffix("?")
          val ann  = textNode.annotations
          res.set(ResponseModel.Name, AmfScalar(name, ann), Annotations.inferred())
          res.set(ResponseModel.StatusCode, AmfScalar(name, ann), Annotations.inferred())
        }

        map.key("description", (ResponseModel.Description in res).allowingAnnotations)

        map.key("headers",
                (ResponseModel.Headers in res using RamlHeaderParser
                  .parse((p: Parameter) => p.adopted(res.id), parseOptional)).treatMapAsArray.optional)

        map.key(
          "body",
          entry => {
            val payloads = mutable.ListBuffer[Payload]()

            val payload = Payload(Annotations(entry))
            payload.adopted(res.id)

            entry.value.tagType match {
              case YType.Null =>
                ctx.factory
                  .typeParser(entry,
                              shape => tracking(shape.withName("default").adopted(payload.id), payload.id),
                              false,
                              defaultType)
                  .parse()
                  .foreach { schema =>
                    schema.annotations += SynthesizedField()
                    payload.annotations += EmptyPayload()
                    ctx.autoGeneratedAnnotation(schema)
                    payloads += payload.set(PayloadModel.Schema, schema, Annotations(entry.value))
                  }
                res.set(RequestModel.Payloads, AmfArray(payloads, Annotations(entry.value)), Annotations(entry))

              case YType.Str =>
                ctx.factory
                  .typeParser(entry,
                              shape => tracking(shape.withName("default").adopted(payload.id), payload.id),
                              false,
                              defaultType)
                  .parse()
                  .foreach(s => {
                    ctx.autoGeneratedAnnotation(s)
                    payloads += payload.set(PayloadModel.Schema, s, Annotations(entry.value))
                  })
                res.set(RequestModel.Payloads, AmfArray(payloads, Annotations(entry.value)), Annotations(entry))

              case _ =>
                // Now we parsed potentially nested shapes for different data types
                entry.value.to[YMap] match {
                  case Right(m) =>
                    val mediaTypeRegexPattern = ".*/.*"
                    m.regex(
                      mediaTypeRegexPattern,
                      entries => {
                        entries.foreach(entry => {
                          payloads += ctx.factory.payloadParser(entry, res.id, false).parse()
                        })
                      }
                    )
                    val entries = m.entries.filter(e => !e.key.as[YScalar].text.matches(mediaTypeRegexPattern))
                    val others  = YMap(entries, entries.headOption.map(_.sourceName).getOrElse(""))
                    if (others.entries.nonEmpty) {
                      if (payloads.isEmpty) {
                        ctx.factory
                          .typeParser(entry,
                                      shape => shape.withName("default").adopted(payload.id),
                                      false,
                                      defaultType)
                          .parse()
                          .foreach { schema =>
                            val payload = res.withPayload()
                            ctx.autoGeneratedAnnotation(schema)
                            payloads += payload
                              .add(Annotations(entry))
                            payload.set(PayloadModel.Schema, tracking(schema, payload.id), Annotations(entry.value))
                          }
                      } else {
                        others.entries.foreach(
                          e =>
                            ctx.eh.violation(
                              UnsupportedExampleMediaTypeErrorSpecification,
                              res.id,
                              s"Unexpected key '${e.key.as[YScalar].text}'. Expecting valid media types.",
                              e))
                      }
                    }
                  case _ =>
                }
                if (payloads.nonEmpty)
                  res.set(RequestModel.Payloads, AmfArray(payloads, Annotations(entry.value)), Annotations(entry))
            }
          }
        )

        map.key(
          "examples".asRamlAnnotation,
          entry => {
            val examples = ExamplesByMediaTypeParser(entry, res.id).parse()
            res.set(ResponseModel.Examples, AmfArray(examples, Annotations(entry.value)), Annotations(entry))
          }
        )

        ctx.closedShape(res.id, map, "response")

        parseMap(res, map)
        res
    }

    response.annotations ++= Annotations(entry)
    response
  }
}