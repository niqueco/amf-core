package amf.core.client.scala.validation.payload

import amf.core.client.common.validation.{StrictValidationMode, ValidationMode}
import amf.core.client.common.{NormalPriority, PluginPriority}
import amf.core.client.scala.model.document.PayloadFragment
import amf.core.client.scala.model.domain.Shape
import amf.core.client.scala.validation.{AMFValidationReport, AMFValidationResult}
import amf.core.internal.plugins.AMFPlugin

import java.io.InputStream
import scala.concurrent.Future

case class ValidatePayloadRequest(shape: Shape, mediaType: String, config: ShapeValidationConfiguration)

trait AMFShapePayloadValidationPlugin extends AMFPlugin[ValidatePayloadRequest] {

  override def priority: PluginPriority = NormalPriority

  override def applies(element: ValidatePayloadRequest): Boolean

  def validator(
      shape: Shape,
      mediaType: String,
      config: ShapeValidationConfiguration,
      validationMode: ValidationMode = StrictValidationMode
  ): AMFShapePayloadValidator

}

trait AMFShapePayloadValidator {

  def validate(payload: String): Future[AMFValidationReport]
  def validate(payloadFragment: PayloadFragment): Future[AMFValidationReport]
  def syncValidate(payload: String): AMFValidationReport
  def syncValidate(stream: InputStream): AMFValidationReport
}

case class PayloadParsingResult(fragment: PayloadFragment, results: List[AMFValidationResult]) {
  def hasError: Boolean = results.nonEmpty
}
