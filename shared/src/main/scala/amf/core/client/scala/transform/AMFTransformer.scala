package amf.core.client.scala.transform

import amf.core.client.scala.{AMFGraphConfiguration, AMFResult}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.transform.pipelines.{TransformationPipeline, TransformationPipelineRunner}
import amf.core.internal.remote.Amf
import amf.core.internal.validation.CoreValidations.ResolutionValidation

object AMFTransformer {

  def transform(unit: BaseUnit, conf: AMFGraphConfiguration): AMFResult = {
    val guessedMediaType = unit.sourceVendor.getOrElse(Amf).mediaType
    transform(unit, PipelineName.from(guessedMediaType, TransformationPipeline.DEFAULT_PIPELINE), conf)
  }

  def transform(unit: BaseUnit, pipelineName: String, conf: AMFGraphConfiguration): AMFResult = {
    val pipelines = conf.registry.transformationPipelines
    val pipeline  = pipelines.get(pipelineName)
    val handler   = conf.errorHandlerProvider.errorHandler()
    val resolved = pipeline match {
      case Some(pipeline) =>
        val runner = TransformationPipelineRunner(handler, conf.listeners.toList)
        runner.run(unit, pipeline)
      case None =>
        handler.violation(
            ResolutionValidation,
            unit.id,
            None,
            s"Cannot find transformation pipeline with name $pipelineName",
            unit.position(),
            unit.location()
        )
        unit
    }
    AMFResult(resolved, handler.getResults)
  }

}
