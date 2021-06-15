package amf.core.client.platform.transform

import amf.core.client.scala.transform.pipelines.{TransformationPipeline => InternalTransformationPipeline}
import amf.core.internal.convert.CoreClientConverters.ClientList
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
trait TransformationPipeline {
  val name: String
  def steps: ClientList[TransformationStep]
}
