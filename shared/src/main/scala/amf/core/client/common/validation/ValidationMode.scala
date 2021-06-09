package amf.core.client.common.validation

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
trait ValidationMode

@JSExportAll
@JSExportTopLevel("ValidationMode")
object ValidationMode {
  val StrictValidationMode: ValidationMode        = amf.core.client.common.validation.StrictValidationMode
  val ScalarRelaxedValidationMode: ValidationMode = amf.core.client.common.validation.ScalarRelaxedValidationMode
}

@JSExportAll
object StrictValidationMode extends ValidationMode
@JSExportAll
object ScalarRelaxedValidationMode extends ValidationMode
