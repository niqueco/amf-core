package amf.core.client.platform

import amf.core.client.platform.model.AmfObjectWrapper
import amf.core.client.platform.model.document.BaseUnit
import amf.core.client.platform.validation.AMFValidationResult
import amf.core.client.platform.validation.AMFValidationResult
import amf.core.client.scala.{
  AMFObjectResult => InternalAMFObjectResult,
  AMFParseResult => InternalAMFParseResult,
  AMFResult => InternalAMFResult
}
import amf.core.internal.convert.CoreClientConverters._
import amf.core.internal.remote.Spec
import amf.core.internal.unsafe.PlatformSecrets

import scala.scalajs.js.annotation.JSExportAll

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

@JSExportAll
class AMFParseResult(private[amf] override val _internal: InternalAMFParseResult) extends AMFResult(_internal) {
  def sourceSpec: Spec = _internal.sourceSpec
}

@JSExportAll
class AMFObjectResult(private[amf] val _internal: InternalAMFObjectResult) extends PlatformSecrets {
  def element: AmfObjectWrapper                = platform.wrap(_internal.element)
  def results: ClientList[AMFValidationResult] = _internal.results.asClient
}
