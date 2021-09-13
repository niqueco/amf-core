package amf.core.internal.transform.pipelines.elements

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.transform.stages.elements.resolution.ElementStageTransformer

abstract class ElementTransformationPipeline[T <: DomainElement](element: T, errorHandler: AMFErrorHandler) {

  val steps: Seq[ElementStageTransformer[T]]

  final def transform(configuration: AMFGraphConfiguration): T = {
    var result: T = element
    steps.foreach { s =>
      s.transform(result, configuration).foreach(result = _)
    }
    result
  }

}
