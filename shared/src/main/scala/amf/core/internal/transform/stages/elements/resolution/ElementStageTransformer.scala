package amf.core.internal.transform.stages.elements.resolution

import amf.core.client.scala.model.domain.DomainElement

abstract class ElementStageTransformer[T<:DomainElement]{

  def transform(element:T):Option[T]
}

trait ElementResolutionStage[T<:DomainElement]{

  def transformer: ElementStageTransformer[T]
}
