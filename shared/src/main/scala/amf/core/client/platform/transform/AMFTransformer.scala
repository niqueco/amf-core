package amf.core.client.platform.transform

import amf.core.client.platform.model.document.BaseUnit
import amf.core.client.platform.{AMFGraphConfiguration, AMFResult}
import amf.core.client.scala.transform.{AMFTransformer => InternalAMFTransformer}
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import amf.core.internal.convert.CoreClientConverters._
@JSExportAll
@JSExportTopLevel("AMFTransformer")
object AMFTransformer {

  def transform(unit: BaseUnit, configuration: AMFGraphConfiguration): AMFResult =
    InternalAMFTransformer.transform(unit, configuration)

  def transform(unit: BaseUnit, pipelineName: String, configuration: AMFGraphConfiguration): AMFResult =
    InternalAMFTransformer.transform(unit, pipelineName, configuration)

}
