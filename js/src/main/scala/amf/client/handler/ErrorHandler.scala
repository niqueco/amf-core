package amf.client.handler

import amf.client.convert.CoreClientConverters._
import amf.client.exported.ErrorHandlerProvider
import amf.client.resolve.ClientErrorHandlerConverter.convertToClient
import amf.client.resolve.{ClientErrorHandler, ClientErrorHandlerConverter}
import amf.client.validate.ValidationResult
import amf.core.errorhandling.UnhandledErrorHandler

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportTopLevel("ErrorHandler")
@JSExportAll
object ErrorHandler {

  def handler(obj: JsErrorHandler): ClientErrorHandler =
    new ClientErrorHandler {
      override def report(result: ValidationResult): Unit     = obj.report(result)
      override def getResults(): ClientList[ValidationResult] = obj.getResults()
    }

  def provider(obj: JsErrorHandler): ErrorHandlerProvider = () => handler(obj)
}

@js.native
trait JsErrorHandler extends js.Object {

  def report(result: ValidationResult): Unit = js.native

  def getResults(): ClientList[ValidationResult] = js.native
}
