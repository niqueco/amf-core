package amf.core.client.platform

import amf.core.client.platform.model.document.{BaseUnit, Document}

import scala.scalajs.js.annotation.JSExportAll
import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.scala.{AMFResult => InternalAMFResult}
import amf.core.client.platform.validation.AMFValidationResult

@JSExportAll
case class AMFResult(private[amf] val _internal: InternalAMFResult) {

  def conforms: Boolean = _internal.conforms

  /**
    * @return list of the resultant [[AMFValidationResult]] of the BaseUnit
    */
  def results: ClientList[AMFValidationResult] = _internal.results.asClient

  /**
    * @return [[BaseUnit]] returned from AMF parse or transform. It can be:
    *  - A [[BaseUnit]] when parsing/transformation is successful
    *  - The most complete unit that could be built, and an [[AMFValidationReport]] report with errors/warnings found
    */
  def baseUnit: BaseUnit = _internal.baseUnit
}
