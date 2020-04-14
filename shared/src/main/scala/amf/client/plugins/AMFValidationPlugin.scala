package amf.client.plugins

import amf.ProfileName
import amf.client.execution.BaseExecutionEnvironment
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.core.validation.core.ValidationProfile
import amf.core.validation.{AMFValidationReport, EffectiveValidations}
import amf.internal.environment.Environment

import scala.concurrent.Future

trait AMFValidationPlugin extends AMFPlugin with PlatformSecrets {

  /**
    * Validation profiles supported by this plugin by default
    */
  def domainValidationProfiles(platform: Platform): Map[String, () => ValidationProfile]

  /**
    * Request for validation of a particular model, profile and list of effective validations form that profile
    */
  def validationRequest(
      baseUnit: BaseUnit,
      profile: ProfileName,
      validations: EffectiveValidations,
      platform: Platform,
      env: Environment,
      resolved: Boolean = false,
      executionEnv: BaseExecutionEnvironment = platform.defaultExecutionEnvironment): Future[AMFValidationReport]
}
