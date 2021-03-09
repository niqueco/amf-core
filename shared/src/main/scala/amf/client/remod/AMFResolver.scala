package amf.client.remod

import amf.client.remod.amfcore.resolution.AMFResolutionPipeline
import amf.core.model.document.BaseUnit

object AMFResolver {

  def resolve(bu:BaseUnit, env: BaseEnvironment):AmfResult = ???

  def resolve(bu:BaseUnit, pipeline: AMFResolutionPipeline, env: BaseEnvironment):AmfResult = ???

}
