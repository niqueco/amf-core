package amf.core.client.scala.validation.payload

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.errorhandling.AMFErrorHandler

import scala.concurrent.ExecutionContext

object ShapeValidationConfiguration {
  def predefined(): ShapeValidationConfiguration = ShapeValidationConfiguration(AMFGraphConfiguration.predefined())
}

case class ShapeValidationConfiguration(private val config: AMFGraphConfiguration) {
  val eh: AMFErrorHandler                = config.errorHandlerProvider.errorHandler()
  val executionContext: ExecutionContext = config.getExecutionContext
  val maxYamlReferences: Option[Int]     = config.options.parsingOptions.maxYamlReferences
}
