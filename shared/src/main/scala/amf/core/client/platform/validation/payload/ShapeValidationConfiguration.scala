package amf.core.client.platform.validation.payload

import amf.core.client.common.remote.Content
import amf.core.client.platform.AMFGraphConfiguration
import amf.core.client.platform.errorhandling.ClientErrorHandler
import amf.core.client.platform.validation.AMFValidationResult
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.validation.payload.{ShapeValidationConfiguration => InternalShapeValidationConfiguration}
import amf.core.internal.convert.CoreClientConverters
import amf.core.internal.convert.CoreClientConverters._

import scala.concurrent.ExecutionContext
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ShapeValidationConfiguration")
object ShapeValidationConfiguration {
  def predefined(): ShapeValidationConfiguration = ShapeValidationConfiguration(AMFGraphConfiguration.predefined())
  def apply(config: AMFGraphConfiguration): ShapeValidationConfiguration =
    new ShapeValidationConfiguration(InternalShapeValidationConfiguration(config))
}

class ShapeValidationConfiguration(private[amf] val _internal: InternalShapeValidationConfiguration) {
  @JSExport
  def newErrorHandler(): AMFErrorHandler = _internal.newErrorHandler()

  @JSExport
  @deprecated("use newErrorHandler instead", "5.0.13")
  val eh: ClientErrorHandler = new ClientErrorHandler {
    override def getResults: CoreClientConverters.ClientList[AMFValidationResult] = _internal.eh.getResults.asClient

    override def report(result: AMFValidationResult): Unit = _internal.eh.report(result)
  }
  val executionContext: ExecutionContext = _internal.executionContext

  @JSExport
  val maxYamlReferences: ClientOption[Int] = _internal.maxYamlReferences.asClient

  // Necessary for Java XML Payload Validator
  @JSExport
  def fetchContent(url: String): ClientFuture[Content] = {
    InternalFutureOps(_internal.fetchContent(url))(ContentMatcher, _internal.config.getExecutionContext).asClient
  }
}
