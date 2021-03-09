package amf.client.remod.amfcore.resolution

import amf.ProfileName
import amf.core.model.document.BaseUnit
import amf.core.model.domain.DomainElement
import amf.core.resolution.stages.selectors.Selector

class AMFResolutionPipeline(name:PipelineName, steps: List[AMFResolutionStep]) {
  def resolve(bu: BaseUnit): BaseUnit = ???

}

trait AMFResolutionStep {
  def apply(bu: BaseUnit): Boolean

  protected var m: Option[BaseUnit] = None
  def resolve(model: BaseUnit): BaseUnit
}

// field resolution step could have sense?

trait ElementResolutionStep[T <: DomainElement] extends AMFResolutionStep {

  protected val selector: Selector

  override def resolve(model: BaseUnit): BaseUnit = ???

  def transform(element: T, isCycle: Boolean): T
}


case class PipelineName(name:String)

object EditingResolutionPipeline extends AMFResolutionPipeline(PipelineName("EDITING"), Nil)
