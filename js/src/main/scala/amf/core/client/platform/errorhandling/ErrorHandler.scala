package amf.core.client.platform.errorhandling

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.validation.AMFValidationResult

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportTopLevel("ErrorHandler")
@JSExportAll
object ErrorHandler {

  def handler(obj: JsErrorHandler): ClientErrorHandler =
    new ClientErrorHandler {
      override def report(result: AMFValidationResult): Unit     = obj.report(result)
      override def getResults(): ClientList[AMFValidationResult] = obj.getResults()
    }

  def provider(obj: JsErrorHandler): ErrorHandlerProvider = () => handler(obj)
}

@js.native
trait JsErrorHandler extends js.Object {

  def report(result: AMFValidationResult): Unit = js.native

  def getResults(): ClientList[AMFValidationResult] = js.native
}
