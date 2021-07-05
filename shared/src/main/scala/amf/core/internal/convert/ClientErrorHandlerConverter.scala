package amf.core.internal.convert

import amf.core.client.platform.errorhandling.ClientErrorHandler
import amf.core.client.platform.validation.{AMFValidationResult => ClientAMFValidationResult}
import amf.core.client.common.position.Range
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.validation.AMFValidationResult
import amf.core.internal.annotations.LexicalInformation
import CoreClientConverters._
object ClientErrorHandlerConverter {

  implicit object RangeToLexicalConverter extends BidirectionalMatcher[LexicalInformation, Range] {

    override def asInternal(from: Range): LexicalInformation = LexicalInformation(from)
    override def asClient(from: LexicalInformation): Range   = from.range
  }

  implicit object ErrorHandlerConverter extends BidirectionalMatcher[AMFErrorHandler, ClientErrorHandler] {

    override def asInternal(from: ClientErrorHandler): AMFErrorHandler = convert(from)
    override def asClient(from: AMFErrorHandler): ClientErrorHandler   = convertToClient(from)
  }

  def convert(clientErrorHandler: ClientErrorHandler): AMFErrorHandler =
    new AMFErrorHandler {

      override def report(result: AMFValidationResult): Unit = clientErrorHandler.report(result)
      override def getResults(): List[AMFValidationResult]   = clientErrorHandler.getResults.asInternal.toList
    }

  def convertToClient(errorHandler: AMFErrorHandler): ClientErrorHandler =
    new ClientErrorHandler {
      override def getResults: CoreClientConverters.ClientList[ClientAMFValidationResult] =
        errorHandler.getResults.asClient

      override def report(result: ClientAMFValidationResult): Unit = errorHandler.report(result)
    }
}
