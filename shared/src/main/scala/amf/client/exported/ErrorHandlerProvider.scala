package amf.client.exported

import amf.client.errorhandling.IgnoringErrorHandler
import amf.client.remod.DefaultErrorHandlerProvider
import amf.client.resolve.ClientErrorHandler
import amf.client.resolve.ClientErrorHandlerConverter.convertToClient
import amf.core.errorhandling.UnhandledErrorHandler

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait ErrorHandlerProvider {
  // Returns a new instance of error handler to collect results
  def errorHandler(): ClientErrorHandler
}

@JSExportAll
object ErrorHandlerProvider {
  def unhandled(): ErrorHandlerProvider = () => convertToClient(UnhandledErrorHandler)
  def default(): ErrorHandlerProvider   = () => convertToClient(DefaultErrorHandlerProvider.errorHandler())
  def ignoring(): ErrorHandlerProvider  = () => convertToClient(IgnoringErrorHandler)
}
