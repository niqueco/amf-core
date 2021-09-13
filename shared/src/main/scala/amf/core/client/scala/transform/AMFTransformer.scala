package amf.core.client.scala.transform

import amf.core.client.common.transform.{PipelineId, PipelineName}
import amf.core.client.scala.{AMFGraphConfiguration, AMFResult}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.remote.Amf
import amf.core.internal.validation.CoreValidations.TransformationValidation

object AMFTransformer {

  /**
    * Transforms a [[BaseUnit]] with a specific configuration and the default pipeline.
    * @param unit [[BaseUnit]] to transform
    * @param configuration [[AMFGraphConfiguration]] required to transform
    * @return [[AMFResult]]
    */
  def transform(unit: BaseUnit, configuration: AMFGraphConfiguration): AMFResult = {
    transform(unit, PipelineId.Default, configuration)
  }

  /**
    * Transforms a [[BaseUnit]] with a specific configuration and a specific pipeline.
    * @param unit [[BaseUnit]] to transform
    * @param pipelineName specific pipeline to use in transformation
    * @param configuration [[AMFGraphConfiguration]] required to transform
    * @return [[AMFResult]]
    */
  def transform(unit: BaseUnit, pipelineName: String, configuration: AMFGraphConfiguration): AMFResult = {
    val pipelines = configuration.registry.transformationPipelines
    val pipeline  = pipelines.get(pipelineName)
    val handler   = configuration.errorHandlerProvider.errorHandler()
    val resolved = pipeline match {
      case Some(pipeline) =>
        val runner = TransformationPipelineRunner(handler, configuration.listeners.toList)
        runner.run(unit, pipeline)
      case None =>
        handler.violation(
            TransformationValidation,
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
