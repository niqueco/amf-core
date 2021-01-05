package amf.core.model.domain

import amf.client.execution.BaseExecutionEnvironment
import amf.core.validation.PayloadValidator
import amf.internal.environment.Environment

trait ValidatorAware {

  def payloadValidator(mediaType: String, exec: BaseExecutionEnvironment): Option[PayloadValidator]

  def payloadValidator(mediaType: String, env: Environment = Environment()): Option[PayloadValidator]

  def parameterValidator(mediaType: String, exec: BaseExecutionEnvironment): Option[PayloadValidator]

  def parameterValidator(mediaType: String, env: Environment = Environment()): Option[PayloadValidator]
}
