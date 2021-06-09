package amf.client.exported.transform

import amf.client.model.document.BaseUnit
import amf.client.resolve.ClientErrorHandler

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("TransformationStepFactory")
object TransformationStepFactory {
  def from(step: JsTransformationStep): TransformationStep =
    (model: BaseUnit, errorHandler: ClientErrorHandler) => step.transform(model, errorHandler)
}

@js.native
trait JsTransformationStep extends js.Object {

  def transform(model: BaseUnit, errorHandler: ClientErrorHandler): BaseUnit = js.native
}
