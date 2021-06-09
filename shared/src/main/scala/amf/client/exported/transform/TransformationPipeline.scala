package amf.client.exported.transform

import amf.client.convert.CoreClientConverters.ClientList
import amf.core.resolution.pipelines.{TransformationPipeline => InternalTransformationPipeline}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
trait TransformationPipeline {
  val name: String
  def steps: ClientList[TransformationStep]
}

@JSExportAll()
@JSExportTopLevel("TransformationPipeline")
object TransformationPipeline {
  val DEFAULT_PIPELINE: String       = InternalTransformationPipeline.DEFAULT_PIPELINE
  val EDITING_PIPELINE: String       = InternalTransformationPipeline.EDITING_PIPELINE
  val COMPATIBILITY_PIPELINE: String = InternalTransformationPipeline.COMPATIBILITY_PIPELINE
  val CACHE_PIPELINE: String         = InternalTransformationPipeline.CACHE_PIPELINE
}
