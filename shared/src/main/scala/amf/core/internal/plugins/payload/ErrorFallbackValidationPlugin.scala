package amf.core.internal.plugins.payload

import amf.core.client.common.validation.{ProfileName, SeverityLevels, ValidationMode}
import amf.core.client.common.{LowPriority, PluginPriority}
import amf.core.client.scala.model.document.PayloadFragment
import amf.core.client.scala.model.domain.Shape
import amf.core.client.scala.validation.payload.{
  AMFShapePayloadValidationPlugin,
  AMFShapePayloadValidator,
  ShapeValidationConfiguration,
  ValidatePayloadRequest
}
import amf.core.client.scala.validation.{AMFValidationReport, AMFValidationResult}
import amf.core.client.scala.vocabulary.Namespace
import amf.core.internal.validation.CoreParserValidations.UnsupportedExampleMediaTypeErrorSpecification
import amf.core.internal.validation.CorePayloadValidations.UnsupportedExampleMediaTypeWarningSpecification

import java.io.InputStream
import scala.concurrent.{ExecutionContext, Future}

private[amf] case class ErrorFallbackValidationPlugin(defaultSeverity: String = SeverityLevels.WARNING)
    extends AMFShapePayloadValidationPlugin {

  override val id: String = "Any match"

  override def applies(element: ValidatePayloadRequest): Boolean = true

  override def validator(
      s: Shape,
      mediaType: String,
      config: ShapeValidationConfiguration,
      validationMode: ValidationMode
  ): AMFShapePayloadValidator =
    ErrorFallbackPayloadValidator(s, mediaType, defaultSeverity)(config.executionContext)

  override def priority: PluginPriority = LowPriority
}

case class ErrorFallbackPayloadValidator(shape: Shape, mediaType: String, defaultSeverity: String)(implicit
    executionContext: ExecutionContext
) extends AMFShapePayloadValidator {

  override def validate(payload: String): Future[AMFValidationReport] = {
    Future.successful(syncValidate(payload))
  }

  override def validate(payloadFragment: PayloadFragment): Future[AMFValidationReport] = {
    val results = createResult(payloadFragment)
    Future.successful(AMFValidationReport("", ProfileName(""), Seq(results)))
  }

  override def syncValidate(payload: String): AMFValidationReport = emptyResult

  override def syncValidate(stream: InputStream): AMFValidationReport = emptyResult

  private def emptyResult: AMFValidationReport = AMFValidationReport("", ProfileName(""), Seq(createResult))

  private def createResult = {
    AMFValidationResult(
      s"Unsupported validation for mediatype: $mediaType and shape ${shape.id}",
      defaultSeverity,
      "",
      Some((Namespace.Document + "value").iri()),
      computeSpecificationId,
      None,
      None,
      null
    )
  }

  private def createResult(fragment: PayloadFragment) = {
    AMFValidationResult(
      s"Unsupported validation for mediatype: ${fragment.mediaType.value()} and shape ${shape.id}",
      defaultSeverity,
      fragment.encodes.id,
      Some((Namespace.Document + "value").iri()),
      computeSpecificationId,
      fragment.encodes.position(),
      fragment.encodes.location(),
      null
    )
  }
  private def computeSpecificationId = {
    if (defaultSeverity == SeverityLevels.VIOLATION) UnsupportedExampleMediaTypeErrorSpecification.id
    else UnsupportedExampleMediaTypeWarningSpecification.id
  }
}
