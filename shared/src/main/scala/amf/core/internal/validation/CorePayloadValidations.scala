package amf.core.internal.validation

import amf.core.client.common.validation.ProfileName
import amf.core.client.common.validation.SeverityLevels.WARNING
import amf.core.client.scala.vocabulary.Namespace
import amf.core.client.scala.vocabulary.Namespace.AmfValidation
import amf.core.internal.validation.core.ValidationSpecification
import amf.core.internal.validation.core.ValidationSpecification.PAYLOAD_VALIDATION

object CorePayloadValidations extends Validations {
  override val specification: String = PAYLOAD_VALIDATION
  override val namespace: Namespace  = AmfValidation

  val UnsupportedExampleMediaTypeWarningSpecification: ValidationSpecification = validation(
    "unsupported-example-media-type-warning",
    "Cannot validate example with unsupported media type"
  )

  override val validations: List[ValidationSpecification] = List(
    UnsupportedExampleMediaTypeWarningSpecification
  )
  override val levels: Map[String, Map[ProfileName, String]] = Map(
    UnsupportedExampleMediaTypeWarningSpecification.id -> all(WARNING)
  )
}
