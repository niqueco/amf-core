package amf.core.client.platform.transform

import amf.core.client.platform.model.document.BaseUnit
import amf.core.client.platform.{AMFGraphConfiguration, AMFResult}
import amf.core.client.scala.transform.{AMFTransformer => InternalAMFTransformer}
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import amf.core.internal.convert.CoreClientConverters._
@JSExportAll
@JSExportTopLevel("AMFTransformer")
object AMFTransformer {

  /**
    * Transforms a [[BaseUnit]] with a specific configuration and the default pipeline.
    * @param unit [[BaseUnit]] to transform
    * @param configuration [[AMFGraphConfiguration]] required to transform
    * @return [[AMFResult]]
    */
  def transform(unit: BaseUnit, configuration: AMFGraphConfiguration): AMFResult =
    InternalAMFTransformer.transform(unit, configuration)

  /**
    * Transforms a [[BaseUnit]] with a specific configuration and a specific pipeline.
    * @param unit [[BaseUnit]] to transform
    * @param pipelineName specific pipeline to use in transformation
    * @param configuration [[AMFGraphConfiguration]] required to transform
    * @return [[AMFResult]]
    */
  def transform(unit: BaseUnit, pipelineName: String, configuration: AMFGraphConfiguration): AMFResult =
    InternalAMFTransformer.transform(unit, pipelineName, configuration)

}
