package amf.core.internal.validation

import amf.core.client.common.validation.{AmlProfile, ProfileName, ProfileNames}
import amf.core.internal.validation.core.ValidationSpecification
import amf.core.client.scala.vocabulary.Namespace

trait Validations {
  protected def validation(
      id: String,
      message: String,
      ramlMessage: Option[String] = None,
      oasMessage: Option[String] = None
  ): ValidationSpecification =
    ValidationSpecification(
      name = (namespace + id).iri(),
      message = message,
      ramlMessage = ramlMessage,
      oasMessage = oasMessage,
      targetInstance = Set(specification)
    )

  protected def all(lvl: String): Map[ProfileName, String] =
    ProfileNames.specProfiles.map(_ -> lvl).toMap + (AmlProfile -> lvl)

  val specification: String
  val namespace: Namespace
  val validations: List[ValidationSpecification]
  val levels: Map[String, Map[ProfileName, String]]
}
