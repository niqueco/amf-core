package amf.core.client.scala.transform

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.document.BaseUnit

trait TransformationStep {
  def transform(model: BaseUnit, errorHandler: AMFErrorHandler, configuration: AMFGraphConfiguration): BaseUnit
}
