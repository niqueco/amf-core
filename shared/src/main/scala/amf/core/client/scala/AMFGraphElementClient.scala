package amf.core.client.scala

import amf.core.client.common.validation.ValidationMode
import amf.core.client.scala.model.document.PayloadFragment
import amf.core.client.scala.model.domain.Shape
import amf.core.client.scala.validation.payload.AMFShapePayloadValidator
import amf.core.internal.plugins.payload.DefaultShapePayloadValidatorFactory

/**
  * Contains common AMF graph operations not associated to documents.
  * Base client for <code>AMLElementClient</code> and <code>AMLElementClient</code>.
  */
class AMFGraphElementClient private[amf] (protected val configuration: AMFGraphConfiguration) {

  def getConfiguration: AMFGraphConfiguration = configuration

  def payloadValidatorFor(shape: Shape, mediaType: String, mode: ValidationMode): AMFShapePayloadValidator =
    DefaultShapePayloadValidatorFactory(configuration).createFor(shape, mediaType, mode)

  def payloadValidatorFor(shape: Shape, fragment: PayloadFragment): AMFShapePayloadValidator =
    DefaultShapePayloadValidatorFactory(configuration).createFor(shape, fragment)

}
