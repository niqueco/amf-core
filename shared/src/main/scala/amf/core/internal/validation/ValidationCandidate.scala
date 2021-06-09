package amf.core.internal.validation

import amf.core.client.scala.model.document.PayloadFragment
import amf.core.client.scala.model.domain.Shape
import amf.core.client.common.validation.SeverityLevels

case class ValidationCandidate(shape: Shape, payload: PayloadFragment)

case class ValidationShapeSet(candidates: Seq[ValidationCandidate], defaultSeverity: String = SeverityLevels.VIOLATION)

object ValidationShapeSet {
  def apply(shape: Shape, payload: PayloadFragment): ValidationShapeSet =
    new ValidationShapeSet(Seq(ValidationCandidate(shape, payload)))
}
