package amf.core.client.platform.transform

import amf.core.client.platform.AMFGraphConfiguration
import amf.core.client.platform.errorhandling.ClientErrorHandler
import amf.core.client.platform.model.document.BaseUnit

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("TransformationStepFactory")
object TransformationStepFactory {
  def from(step: JsTransformationStep): TransformationStep =
    (model: BaseUnit, errorHandler: ClientErrorHandler, configuration: AMFGraphConfiguration) =>
      step.transform(model, errorHandler, configuration)
}

@js.native
trait JsTransformationStep extends js.Object {

  def transform(model: BaseUnit, errorHandler: ClientErrorHandler, configuration: AMFGraphConfiguration): BaseUnit =
    js.native
}
