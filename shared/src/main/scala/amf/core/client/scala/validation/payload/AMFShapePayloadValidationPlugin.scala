package amf.core.client.scala.validation.payload

import amf.core.client.common.validation.{StrictValidationMode, ValidationMode}
import amf.core.client.common.{NormalPriority, PluginPriority}
import amf.core.client.scala.model.document.PayloadFragment
import amf.core.client.scala.model.domain.Shape
import amf.core.client.scala.validation.{AMFValidationReport, AMFValidationResult}
import amf.core.internal.plugins.AMFPlugin
import amf.core.internal.validation.ValidationConfiguration

import scala.concurrent.{ExecutionContext, Future}

case class ValidatePayloadRequest(shape: Shape, mediaType: String, config: ValidationConfiguration)

trait AMFShapePayloadValidationPlugin extends AMFPlugin[ValidatePayloadRequest] {

  override def priority: PluginPriority = NormalPriority

  override def applies(element: ValidatePayloadRequest): Boolean

  // TODO ARM we can remove the validation mode and handle it on different plugins, o we can put the mode into the options
  def validator(shape: Shape,
                mediaType: String,
                config: ValidationConfiguration,
                validationMode: ValidationMode = StrictValidationMode): AMFShapePayloadValidator

}

trait AMFShapePayloadValidator {

  def validate(payload: String)(implicit executionContext: ExecutionContext): Future[AMFValidationReport]
  def validate(payloadFragment: PayloadFragment)(
      implicit executionContext: ExecutionContext): Future[AMFValidationReport]
  def syncValidate(payload: String): AMFValidationReport
}

case class PayloadParsingResult(fragment: PayloadFragment, results: List[AMFValidationResult]) {
  def hasError: Boolean = results.nonEmpty
}
