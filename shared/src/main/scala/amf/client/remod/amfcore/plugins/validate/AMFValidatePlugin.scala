package amf.client.remod.amfcore.plugins.validate

import amf.ProfileName
import amf.client.remod.amfcore.plugins.AMFPlugin
import amf.core.model.document.BaseUnit
import amf.core.unsafe.PlatformSecrets
import amf.core.validation.{AMFValidationReport, EffectiveValidations}
import amf.internal.environment.Environment

import scala.concurrent.{ExecutionContext, Future}

// TODO: profileName should be opt-in
/**
  * TODO: environment is only there for the AMFPayloadValidationPlugin -> canValidate(Shape, Environment). The Environment is never used (checked XML also).
  */
class ValidationOptions(var profileName: ProfileName,
                        var environment: Environment,
                        var validations: EffectiveValidations)

// TODO: this class shouldn't exist, used to propagate the resolved model
case class ValidationResult(unit: BaseUnit, report: AMFValidationReport)

trait AMFValidatePlugin extends AMFPlugin[BaseUnit] with PlatformSecrets{
  def validate(unit: BaseUnit, options: ValidationOptions)(implicit executionContext: ExecutionContext): Future[ValidationResult]
}
