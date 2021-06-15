package amf.core.client.scala.transform.pipelines

import amf.core.client.scala.transform.PipelineName
import amf.core.client.scala.transform.stages.{ReferenceResolutionStage, TransformationStep}
import amf.core.internal.plugins.parse.AMFGraphParsePlugin

class BasicTransformationPipeline private (override val name: String) extends TransformationPipeline() {
  private def references = new ReferenceResolutionStage(keepEditingInfo = false)

  override def steps: Seq[TransformationStep] = Seq(references)
}

object BasicTransformationPipeline {
  val name: String           = PipelineName.from(AMFGraphParsePlugin.id, TransformationPipeline.DEFAULT_PIPELINE)
  def apply()                = new BasicTransformationPipeline(name)
  private[amf] def editing() = new BasicTransformationPipeline(BasicEditingTransformationPipeline.name)
}

object BasicEditingTransformationPipeline {
  val name: String = PipelineName
    .from(AMFGraphParsePlugin.id, TransformationPipeline.EDITING_PIPELINE)
  def apply(): BasicTransformationPipeline = BasicTransformationPipeline.editing()
}
