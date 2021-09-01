package amf.core.client.platform

import amf.core.client.common.validation.ValidationMode
import amf.core.client.platform.model.document.PayloadFragment
import amf.core.client.platform.model.domain.Shape
import amf.core.client.platform.validation.payload.AMFShapePayloadValidator
import amf.core.client.scala.{AMFGraphElementClient => InternalAMFElementClient}
import amf.core.internal.convert.CoreClientConverters._
import amf.core.internal.convert.PayloadValidatorConverter.PayloadValidatorMatcher

import scala.concurrent.ExecutionContext
import scala.scalajs.js.annotation.JSExportAll

/**
  * Contains common AMF graph operations not associated to documents.
  * Base client for <code>AMLElementClient</code> and <code>AMLElementClient</code>.
  */
@JSExportAll
class AMFGraphElementClient private[amf] (private val _internal: InternalAMFElementClient) {

  private[amf] def this(configuration: AMFGraphConfiguration) = {
    this(new InternalAMFElementClient(configuration))
  }
  private implicit val ec: ExecutionContext = getConfiguration().getExecutionContext

  def getConfiguration(): AMFGraphConfiguration = _internal.getConfiguration

  def payloadValidatorFor(shape: Shape, mediaType: String, mode: ValidationMode): AMFShapePayloadValidator =
    PayloadValidatorMatcher.asClient(_internal.payloadValidatorFor(shape._internal, mediaType, mode))
  def payloadValidatorFor(shape: Shape, fragment: PayloadFragment): AMFShapePayloadValidator =
    PayloadValidatorMatcher.asClient(_internal.payloadValidatorFor(shape._internal, fragment._internal))

}
