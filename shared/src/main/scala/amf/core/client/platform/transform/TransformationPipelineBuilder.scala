package amf.core.client.platform.transform

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import amf.core.client.scala.transform.{TransformationPipelineBuilder => InternalTransformationPipelineBuilder}
import amf.core.internal.convert.CoreClientConverters._
import amf.core.internal.convert.TransformationPipelineConverter._
import amf.core.client.scala.{AMFGraphConfiguration, transform}

@JSExportAll
case class TransformationPipelineBuilder(private[amf] val _internal: transform.TransformationPipelineBuilder) {

  def build(): TransformationPipeline = _internal.build()

  def withName(newName: String): TransformationPipelineBuilder = _internal.withName(newName)

  /**
    * inserts stage at the end of the pipeline
    */
  def append(newStage: TransformationStep): TransformationPipelineBuilder = _internal.append(newStage)

  /**
    * inserts stage at the beginning of pipeline
    */
  def prepend(newStage: TransformationStep): TransformationPipelineBuilder = _internal.prepend(newStage)
}

@JSExportTopLevel("TransformationPipelineBuilder")
@JSExportAll
object TransformationPipelineBuilder {

  def empty(pipelineName: String): TransformationPipelineBuilder =
    InternalTransformationPipelineBuilder.empty(pipelineName)

  def fromPipeline(pipeline: TransformationPipeline): TransformationPipelineBuilder =
    InternalTransformationPipelineBuilder.fromPipeline(pipeline)

  def fromPipeline(pipelineName: String, conf: AMFGraphConfiguration): ClientOption[TransformationPipelineBuilder] =
    InternalTransformationPipelineBuilder.fromPipeline(pipelineName, conf).asClient
}
