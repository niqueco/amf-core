package amf.core.internal.plugins.payload

import amf.core.client.common.validation.ValidationMode
import amf.core.client.scala.model.document.PayloadFragment
import amf.core.client.scala.model.domain.Shape
import amf.core.client.scala.validation.payload.AMFShapePayloadValidator

trait ShapePayloadValidatorFactory {

  def createFor(shape: Shape, mediaType: String, mode: ValidationMode): AMFShapePayloadValidator

  def createFor(shape: Shape, fragment: PayloadFragment): AMFShapePayloadValidator
}
