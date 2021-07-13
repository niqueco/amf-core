package amf.core.client.platform.errorhandling

import amf.core.client.platform.validation.AMFValidationResult
import amf.core.internal.convert.CoreClientConverters.ClientList

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait ClientErrorHandler {

  def getResults: ClientList[AMFValidationResult]

  def report(result: AMFValidationResult): Unit
}
