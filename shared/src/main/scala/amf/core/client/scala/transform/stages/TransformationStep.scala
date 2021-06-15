package amf.core.client.scala.transform.stages

import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.document.BaseUnit

trait TransformationStep {
  def transform(model: BaseUnit, errorHandler: AMFErrorHandler): BaseUnit
}
