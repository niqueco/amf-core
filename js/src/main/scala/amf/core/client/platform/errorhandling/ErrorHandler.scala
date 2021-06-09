package amf.core.client.platform.errorhandling

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.validation.ValidationResult

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
