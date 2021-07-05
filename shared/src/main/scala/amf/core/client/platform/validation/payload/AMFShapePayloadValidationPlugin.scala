package amf.core.client.platform.validation.payload

import amf.core.client.common.validation.{StrictValidationMode, ValidationMode}
import amf.core.client.common.{NormalPriority, PluginPriority}
import amf.core.client.platform.model.document.PayloadFragment
import amf.core.client.platform.model.domain.Shape
import amf.core.client.platform.plugin.AMFPlugin
import amf.core.client.platform.validation.AMFValidationResult
import amf.core.client.scala.validation.payload.{ValidatePayloadRequest => InternalValidatePayloadRequest}
import amf.core.internal.convert.CoreClientConverters._

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
case class ValidatePayloadRequest private[amf] (_internal: InternalValidatePayloadRequest) {
  def shape: Shape                         = _internal.shape
  def mediaType: String                    = _internal.mediaType
  def config: ShapeValidationConfiguration = _internal.config
}

@JSExportAll
trait AMFShapePayloadValidationPlugin extends AMFPlugin[ValidatePayloadRequest] {

  def priority: PluginPriority = NormalPriority

  def applies(element: ValidatePayloadRequest): Boolean

  def validator(shape: Shape,
                mediaType: String,
                config: ShapeValidationConfiguration,
                validationMode: ValidationMode = StrictValidationMode): AMFShapePayloadValidator
}

@JSExportAll
case class PayloadParsingResult private[amf] (fragment: PayloadFragment, results: List[AMFValidationResult]) {
  def hasError: Boolean = results.nonEmpty
}
