package amf.core.client.common.validation
import amf.core.internal.convert.CoreClientConverters._

import scala.scalajs.js.annotation.{JSExport, JSExportAll, JSExportTopLevel}
import amf.core.internal.validation.core.{ValidationProfile => InternalValidationProfile}

@JSExportAll
class ValidationProfile private[amf] (private[amf] val internal: InternalValidationProfile) {

  def profileName(): ProfileName               = internal.name
  def baseProfile(): ClientOption[ProfileName] = internal.baseProfile.asClient
}
