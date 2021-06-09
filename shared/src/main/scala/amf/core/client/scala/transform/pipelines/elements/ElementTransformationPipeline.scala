package amf.core.client.scala.transform.pipelines.elements

import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.transform.stages.elements.resolution.ElementStageTransformer

abstract class ElementTransformationPipeline[T <: DomainElement](element: T, errorHandler: AMFErrorHandler) {

  val steps: Seq[ElementStageTransformer[T]]

  final def resolve(): T = {
    var result: T = element
    steps.foreach { s =>
      s.transform(result).foreach(result = _)
    }
    result
  }

}
