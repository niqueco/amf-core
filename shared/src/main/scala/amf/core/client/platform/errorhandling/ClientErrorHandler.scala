package amf.core.client.platform.errorhandling

import amf.core.client.platform.validation.AMFValidationResult
import amf.core.internal.convert.CoreClientConverters.ClientList

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait ClientErrorHandler {

  /** Get all [[AMFValidationResult]] reported */
  def getResults: ClientList[AMFValidationResult]

  /** Report an [[AMFValidationResult]] */
  def report(result: AMFValidationResult): Unit
}
