package amf.core.client.platform.validation.payload

import amf.core.client.common.validation.{StrictValidationMode, ValidationMode}
import amf.core.client.platform.model.document.PayloadFragment
import amf.core.client.platform.model.domain.Shape
import amf.core.client.platform.plugins.JsAMFPlugin
import amf.core.client.platform.validation.AMFValidationReport
import amf.core.internal.convert.CoreClientConverters
import amf.core.internal.convert.CoreClientConverters._

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@js.native
trait JsAMFPayloadValidationPlugin extends JsAMFPlugin {

  def id: String = js.native

  def applies(element: ValidatePayloadRequest): Boolean = js.native

  def validator(shape: Shape,
                mediaType: String,
                config: ShapeValidationConfiguration,
                validationMode: ValidationMode = StrictValidationMode): JsPayloadValidator
}

@js.native
trait JsPayloadValidator extends js.Object {

  def validate(payload: String): ClientFuture[AMFValidationReport] = js.native

  def validate(payloadFragment: PayloadFragment): ClientFuture[AMFValidationReport] = js.native

  def syncValidate(payload: String): AMFValidationReport = js.native
}

@JSExportAll
@JSExportTopLevel("AMFPayloadValidationPluginConverter")
object AMFPayloadValidationPluginConverter {
  def toAMF(plugin: JsAMFPayloadValidationPlugin): AMFShapePayloadValidationPlugin = {
    new AMFShapePayloadValidationPlugin {
      override def applies(element: ValidatePayloadRequest): Boolean = plugin.applies(element)

      override def validator(shape: Shape,
                             mediaType: String,
                             config: ShapeValidationConfiguration,
                             validationMode: ValidationMode): AMFShapePayloadValidator = {
        toAMF(plugin.validator(shape, mediaType, config, validationMode))
      }

      override val id: String = plugin.id
    }
  }

  private[amf] def toAMF(validator: JsPayloadValidator): AMFShapePayloadValidator = {
    new AMFShapePayloadValidator {
      override def validate(payload: String): ClientFuture[AMFValidationReport] = validator.validate(payload)

      override def validate(payloadFragment: PayloadFragment): ClientFuture[AMFValidationReport] =
        validator.validate(payloadFragment)

      override def syncValidate(payload: String): AMFValidationReport = validator.syncValidate(payload)
    }
  }
}
