package amf.client.remod.amfcore.plugins.validate

import amf.ProfileName
import amf.client.remod.amfcore.plugins.AMFPlugin
import amf.core.model.document.BaseUnit
import amf.core.unsafe.PlatformSecrets
import amf.core.validation.{AMFValidationReport, EffectiveValidations}

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
