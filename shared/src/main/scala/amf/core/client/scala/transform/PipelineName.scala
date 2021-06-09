package amf.core.client.scala.transform

import amf.core.client.scala.transform.pipelines.TransformationPipeline

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportTopLevel("PipelineName")
@JSExportAll
object PipelineName {
  def from(targetMediaType: String, pipelineId: String): String = s"$targetMediaType+$pipelineId"
  def from(targetMediaType: String): String                     = s"$targetMediaType+${TransformationPipeline.DEFAULT_PIPELINE}"
}
