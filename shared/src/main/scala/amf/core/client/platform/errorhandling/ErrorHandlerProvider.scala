package amf.core.client.platform.errorhandling

import amf.core.client.scala.errorhandling.{DefaultErrorHandlerProvider, IgnoringErrorHandler, UnhandledErrorHandler}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import amf.core.internal.convert.ClientErrorHandlerConverter.convertToClient

@JSExportAll
trait ErrorHandlerProvider {
  // Returns a new instance of error handler to collect results
  def errorHandler(): ClientErrorHandler
}

@JSExportAll
@JSExportTopLevel("ErrorHandlerProvider")
object ErrorHandlerProvider {
  def unhandled(): ErrorHandlerProvider = () => convertToClient(UnhandledErrorHandler)
  def default(): ErrorHandlerProvider   = () => convertToClient(DefaultErrorHandlerProvider.errorHandler())
  def ignoring(): ErrorHandlerProvider  = () => convertToClient(IgnoringErrorHandler)
}
