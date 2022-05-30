package amf.core.client.scala.transform

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.config._
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.document.{BaseUnit, BaseUnitProcessingData}

trait TransformationPipeline {
  val name: String
  def steps: Seq[TransformationStep]
}

// transformation pipelines can only run internally within amf.
private[amf] case class TransformationPipelineRunner(
    errorHandler: AMFErrorHandler,
    configuration: AMFGraphConfiguration
) {

  private def notifyEvent(e: AMFEvent): Unit = configuration.listeners.foreach(_.notifyEvent(e))

  def run(model: BaseUnit, pipeline: TransformationPipeline): BaseUnit = {
    notifyEvent(StartingTransformationEvent(pipeline))
    var transformedModel = model
    val steps            = pipeline.steps
    steps.zipWithIndex foreach { case (step, index) =>
      notifyEvent(StartedTransformationStepEvent(step, index))
      transformedModel = step.transform(transformedModel, errorHandler, configuration)
      notifyEvent(FinishedTransformationStepEvent(step, index))
    }

    val processingData = Option(transformedModel.processingData).getOrElse(BaseUnitProcessingData())
    processingData.withTransformed(true)

    notifyEvent(FinishedTransformationEvent(transformedModel))
    transformedModel
  }
}
