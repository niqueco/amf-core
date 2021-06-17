package amf.core.client.platform.validation.payload

import amf.core.client.common.validation.ValidationMode
import amf.core.client.platform.model.document.PayloadFragment
import amf.core.client.platform.model.domain.Shape

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait ShapePayloadValidatorFactory {
  def createFor(shape: Shape, mediaType: String, mode: ValidationMode): AMFShapePayloadValidator
  def createFor(shape: Shape, fragment: PayloadFragment): AMFShapePayloadValidator
}
