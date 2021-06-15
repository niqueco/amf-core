package amf.core.internal.validation

import amf.core.client.common.validation.ProfileName
import amf.core.client.scala.vocabulary.Namespace
import amf.core.client.scala.vocabulary.Namespace.AmfParser
import amf.core.internal.validation.core.ValidationSpecification
import amf.core.internal.validation.core.ValidationSpecification.PARSER_SIDE_VALIDATION

object CoreParserValidations extends Validations {

  override val specification: String = PARSER_SIDE_VALIDATION
  override val namespace: Namespace  = AmfParser

  val UnsupportedExampleMediaTypeErrorSpecification: ValidationSpecification = validation(
      "unsupported-example-media-type",
      "Cannot validate example with unsupported media type"
  )
  override val validations: List[ValidationSpecification] = List(
      UnsupportedExampleMediaTypeErrorSpecification
  )
  override val levels: Map[String, Map[ProfileName, String]] = Map.empty
}
