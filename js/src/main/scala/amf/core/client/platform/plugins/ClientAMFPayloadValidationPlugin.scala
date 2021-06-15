package amf.core.client.platform.plugins

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.model.document.PayloadFragment
import amf.core.client.platform.model.domain.Shape
import amf.core.client.platform.validation.AMFValidationReport
import amf.core.client.common.validation.{StrictValidationMode, ValidationMode}
import amf.core.internal.validation.ValidationConfiguration

import scala.scalajs.js

@js.native
trait ClientAMFPayloadValidationPlugin extends ClientAMFPlugin {

  val payloadMediaType: ClientList[String] = js.native

  def canValidate(shape: Shape, config: ValidationConfiguration): Boolean = js.native

  def validator(s: Shape,
                config: ValidationConfiguration,
                validationMode: ValidationMode = StrictValidationMode): ClientPayloadValidator
}

@js.native
trait ClientPayloadValidator extends js.Object {

  val shape: Shape
  val defaultSeverity: String
  val validationMode: ValidationMode
  val config: ValidationConfiguration

  def validate(payload: String, mediaType: String): ClientFuture[AMFValidationReport] = js.native

  def validate(payloadFragment: PayloadFragment): ClientFuture[AMFValidationReport] = js.native

  def syncValidate(payload: String, mediaType: String): AMFValidationReport = js.native

  def isValid(payload: String, mediaType: String): ClientFuture[Boolean] = js.native
}
