package amf.client.`new`

import amf.client.parse.DefaultParserErrorHandler
import amf.core.errorhandling.ErrorHandler

trait ErrorHandlerProvider {

  // Returns a new instance of error handler to collect results
  def errorHandler(): ErrorHandler
}

object DefaultErrorHandlerProvider extends ErrorHandlerProvider {
  override def errorHandler(): ErrorHandler = DefaultParserErrorHandler()
}
