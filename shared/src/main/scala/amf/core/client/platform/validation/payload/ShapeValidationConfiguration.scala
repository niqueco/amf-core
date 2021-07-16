package amf.core.client.platform.validation.payload

import amf.core.client.platform.AMFGraphConfiguration
import amf.core.client.platform.errorhandling.ClientErrorHandler
import amf.core.client.platform.validation.AMFValidationResult
import amf.core.client.scala.validation.payload.{ShapeValidationConfiguration => InternalShapeValidationConfiguration}
import amf.core.internal.convert.CoreClientConverters

import scala.concurrent.ExecutionContext
import scala.scalajs.js.annotation.{JSExport, JSExportAll, JSExportTopLevel}
import amf.core.internal.convert.CoreClientConverters._

@JSExportTopLevel("ShapeValidationConfiguration")
object ShapeValidationConfiguration {
  def predefined(): ShapeValidationConfiguration = ShapeValidationConfiguration(AMFGraphConfiguration.predefined())
  def apply(config: AMFGraphConfiguration): ShapeValidationConfiguration =
    new ShapeValidationConfiguration(InternalShapeValidationConfiguration(config))
}

class ShapeValidationConfiguration(private[amf] val _internal: InternalShapeValidationConfiguration) {
  @JSExport
  val eh: ClientErrorHandler = new ClientErrorHandler {
    override def getResults: CoreClientConverters.ClientList[AMFValidationResult] = _internal.eh.getResults.asClient

    override def report(result: AMFValidationResult): Unit = _internal.eh.report(result)
  }
  val executionContext: ExecutionContext = _internal.executionContext
  @JSExport
  val maxYamlReferences: ClientOption[Int] = _internal.maxYamlReferences.asClient
}
