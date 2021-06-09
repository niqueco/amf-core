package amf.client.validate

import amf.ProfileName
import amf.client.convert.CoreClientConverters._

import scala.scalajs.js.annotation.{JSExport, JSExportAll}
import amf.core.validation.core.{ValidationProfile => InternalValidationProfile}

@JSExportAll
class ValidationProfile private[amf] (private[amf] val internal: InternalValidationProfile) {

  def profileName(): ProfileName               = internal.name
  def baseProfile(): ClientOption[ProfileName] = internal.baseProfile.asClient
}
