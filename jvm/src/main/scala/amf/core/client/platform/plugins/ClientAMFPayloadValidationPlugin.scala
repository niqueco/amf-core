package amf.core.client.platform.plugins

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.AMFGraphConfiguration
import amf.core.client.platform.model.document.PayloadFragment
import amf.core.client.platform.model.domain.Shape
import amf.core.client.platform.validation.AMFValidationReport
import amf.core.client.common.validation.{StrictValidationMode, ValidationMode}
import amf.core.internal.validation.ValidationConfiguration

import scala.concurrent.ExecutionContext
import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait ClientAMFPayloadValidationPlugin extends ClientAMFPlugin {

  val payloadMediaType: ClientList[String]

  def canValidate(shape: Shape, config: ValidationConfiguration): Boolean

  def validator(s: Shape,
                config: ValidationConfiguration,
                validationMode: ValidationMode = StrictValidationMode): ClientPayloadValidator
}

trait ClientPayloadValidator {

  val shape: Shape
  val defaultSeverity: String
  val validationMode: ValidationMode
  val config: ValidationConfiguration

  def validate(payload: String, mediaType: String)(
      implicit executionContext: ExecutionContext): ClientFuture[AMFValidationReport]

  def validate(payloadFragment: PayloadFragment)(
      implicit executionContext: ExecutionContext): ClientFuture[AMFValidationReport]

  def syncValidate(mediaType: String, payload: String): AMFValidationReport

  def isValid(payload: String, mediaType: String)(implicit executionContext: ExecutionContext): ClientFuture[Boolean]
}
