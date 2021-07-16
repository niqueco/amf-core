package amf.core.client.scala.transform

import amf.core.client.scala.config._
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.document.BaseUnit

trait TransformationPipeline {
  val name: String
  def steps: Seq[TransformationStep]
}

// transformation pipelines can only run internally within amf.
private[amf] case class TransformationPipelineRunner(errorHandler: AMFErrorHandler,
                                                     listeners: Seq[AMFEventListener] = Nil) {

  private def notifyEvent(e: AMFEvent): Unit = listeners.foreach(_.notifyEvent(e))

  def run(model: BaseUnit, pipeline: TransformationPipeline): BaseUnit = {
    notifyEvent(StartingTransformationEvent(pipeline))
    var transformedModel = model
    val steps            = pipeline.steps
    steps.zipWithIndex foreach {
      case (step, index) =>
        notifyEvent(StartedTransformationStepEvent(step, index))
        transformedModel = step.transform(transformedModel, errorHandler)
        notifyEvent(FinishedTransformationStepEvent(step, index))
    }
    // TODO: should be unit metadata
    transformedModel.resolved = true
    notifyEvent(FinishedTransformationEvent(transformedModel))
    transformedModel
  }
}
