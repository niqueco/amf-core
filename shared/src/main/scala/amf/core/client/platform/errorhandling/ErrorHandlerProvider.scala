package amf.core.client.platform.errorhandling

import amf.core.client.scala.errorhandling.{DefaultErrorHandlerProvider, IgnoringErrorHandler, UnhandledErrorHandler}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import amf.core.internal.convert.ClientErrorHandlerConverter.convertToClient

@JSExportAll
trait ErrorHandlerProvider {

  /** Return a new instance of error handler to collect results */
  def errorHandler(): ClientErrorHandler
}

@JSExportAll
@JSExportTopLevel("ErrorHandlerProvider")
object ErrorHandlerProvider {

  /** Error handler that throws Exception when a constraint is reported */
  def unhandled(): ErrorHandlerProvider = () => convertToClient(UnhandledErrorHandler)

  /** Default error handler */
  def default(): ErrorHandlerProvider = () => convertToClient(DefaultErrorHandlerProvider.errorHandler())

  /** Error handler that ignores error reports */
  def ignoring(): ErrorHandlerProvider = () => convertToClient(IgnoringErrorHandler)
}
