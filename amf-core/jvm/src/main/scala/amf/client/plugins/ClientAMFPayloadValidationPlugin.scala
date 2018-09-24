package amf.client.plugins

import amf.client.model.document.PayloadFragment
import amf.client.model.domain.Shape
import amf.client.validate.{PayloadParsingResult => ExternalPayloadParsingResult, ValidationReport, ValidationShapeSet}
import amf.client.convert.CoreClientConverters._
import amf.client.environment.Environment

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait ClientAMFPayloadValidationPlugin extends ClientAMFPlugin {

  def parsePayload(payload: String, mediaType: String, env: Environment, shape: Shape): PayloadFragment

  def parsePayloadWithErrorHandler(payload: String,
                                   mediaType: String,
                                   env: Environment,
                                   shape: Shape): ExternalPayloadParsingResult

  def validateSet(set: ValidationShapeSet, env: Environment): ClientFuture[ValidationReport]

  val payloadMediaType: ClientList[String]

  def canValidate(shape: Shape, env: Environment): Boolean
}