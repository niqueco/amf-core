package amf.core.client.scala.errorhandling

trait ErrorHandlerProvider {

  // Returns a new instance of error handler to collect results
  def errorHandler(): AMFErrorHandler
}

object DefaultErrorHandlerProvider extends ErrorHandlerProvider {
  override def errorHandler(): AMFErrorHandler = DefaultErrorHandler()
}
