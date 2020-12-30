package amf.client.model.domain

import amf.client.convert.CoreClientConverters.ClientOption
import amf.client.environment.Environment
import amf.client.execution.BaseExecutionEnvironment
import amf.client.validate.PayloadValidator

trait ValidatorAware {

  def payloadValidator(mediaType: String): ClientOption[PayloadValidator]

  def payloadValidator(mediaType: String, exec: BaseExecutionEnvironment): ClientOption[PayloadValidator]

  def payloadValidator(mediaType: String, env: Environment): ClientOption[PayloadValidator]

  def parameterValidator(mediaType: String): ClientOption[PayloadValidator]

  def parameterValidator(mediaType: String, exec: BaseExecutionEnvironment): ClientOption[PayloadValidator]

  def parameterValidator(mediaType: String, env: Environment): ClientOption[PayloadValidator]
}
