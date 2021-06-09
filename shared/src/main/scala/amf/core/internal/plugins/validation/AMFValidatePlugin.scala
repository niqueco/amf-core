package amf.core.internal.plugins.validation

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.validation.AMFValidationReport
import amf.core.client.common.validation.ProfileName
import amf.core.internal.plugins.AMFPlugin
import amf.core.internal.unsafe.PlatformSecrets
import amf.core.internal.validation.{EffectiveValidations, ValidationConfiguration}

import scala.concurrent.{ExecutionContext, Future}

trait AMFValidatePlugin extends AMFPlugin[ValidationInfo] with PlatformSecrets {
  def validate(unit: BaseUnit, options: ValidationOptions)(
      implicit executionContext: ExecutionContext): Future[ValidationResult]
}

case class ValidationOptions(profile: ProfileName,
                             effectiveValidations: EffectiveValidations,
                             config: ValidationConfiguration)

case class ValidationInfo(val baseUnit: BaseUnit, val profile: ProfileName)

case class ValidationResult(unit: BaseUnit, report: AMFValidationReport)
