package amf.core.client.platform.validation

import amf.core.client.scala.validation.{AMFValidationReport => InternalValidationReport}
import amf.core.client.common.validation.ProfileName
import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.scala.validation.{AMFValidationReport => InternalValidationReport}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
class AMFValidationReport(private[amf] val _internal: InternalValidationReport) {

  @JSExportTopLevel("client.validate.AMFValidationReport")
  def this(model: String, profile: ProfileName, results: ClientList[ValidationResult]) =
    this(InternalValidationReport(model, profile, results.asInternal))

  def conforms: Boolean                     = _internal.conforms
  def model: String                         = _internal.model
  def profile: ProfileName                  = _internal.profile
  def results: ClientList[ValidationResult] = _internal.results.asClient

  override def toString: String = _internal.toString

  // Name is not 'toString' as it clashes in Typescript definition
  def toStringMaxed(max: Int): String = _internal.toString(max)
}
