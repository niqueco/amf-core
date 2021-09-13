package amf.core.client.platform.transform

import amf.core.client.platform.AMFGraphConfiguration
import amf.core.client.platform.errorhandling.ClientErrorHandler
import amf.core.client.platform.model.document.BaseUnit

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait TransformationStep {
  def transform(model: BaseUnit, errorHandler: ClientErrorHandler, configuration: AMFGraphConfiguration): BaseUnit
}
