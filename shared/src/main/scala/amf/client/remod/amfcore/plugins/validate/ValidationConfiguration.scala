package amf.client.remod.amfcore.plugins.validate

import amf.ProfileName
import amf.client.remod.AMFGraphConfiguration
import amf.core.errorhandling.AMFErrorHandler
import amf.core.validation.core.ValidationProfile

import scala.concurrent.ExecutionContext

object ValidationConfiguration {
  def predefined(): ValidationConfiguration = new ValidationConfiguration(AMFGraphConfiguration.predefined())
}

case class ValidationConfiguration(amfConfig: AMFGraphConfiguration) {

  val eh: AMFErrorHandler                              = amfConfig.errorHandlerProvider.errorHandler()
  val executionContext: ExecutionContext               = amfConfig.getExecutionContext
  val maxYamlReferences: Option[Long]                  = amfConfig.options.parsingOptions.maxYamlReferences
  val constraints: Map[ProfileName, ValidationProfile] = amfConfig.registry.constraintsRules
}
