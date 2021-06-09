package amf.core.client.platform.errorhandling

import amf.core.client.platform.validation.ValidationResult
import amf.core.internal.convert.CoreClientConverters.ClientList

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait ClientErrorHandler {

  def getResults: ClientList[ValidationResult]

  def report(result: ValidationResult): Unit
}
