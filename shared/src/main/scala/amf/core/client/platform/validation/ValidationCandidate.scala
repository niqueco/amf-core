package amf.core.client.platform.validation

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.model.document.PayloadFragment
import amf.core.client.platform.model.domain.Shape
import amf.core.internal.validation.{ValidationCandidate => InternalValidationCandidate}
import amf.core.internal.validation.{ValidationShapeSet => InternalValidationShapeSet}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
case class ValidationShapeSet(private[amf] val _internal: InternalValidationShapeSet) {

  @JSExportTopLevel("ValidationShapeSet")
  def this(candidates: ClientList[ValidationCandidate], closure: ClientList[Shape], defaultSeverity: String) =
    this(InternalValidationShapeSet(candidates.asInternal, defaultSeverity))

  def candidates: ClientList[ValidationCandidate] = _internal.candidates.asClient

  def defaultSeverity: String = _internal.defaultSeverity
}

@JSExportAll
case class ValidationCandidate(private[amf] val _internal: InternalValidationCandidate) {

  @JSExportTopLevel("ValidationCandidate")
  def this(shape: Shape, payload: PayloadFragment) =
    this(InternalValidationCandidate(shape._internal, payload._internal))

  def shape: Shape = _internal.shape

  def payload: PayloadFragment = _internal.payload
}
