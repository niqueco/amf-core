package amf.client.`new`

import amf.ProfileName
import amf.core.model.document.BaseUnit

object AMFResolver {

  def resolve(bu:BaseUnit, env: BaseEnvironment):AmfResult = ???

  def resolve(bu:BaseUnit, pipeline: AmfResolutionPipeline, env: BaseEnvironment):AmfResult = ???

}

case class AmfResolutionPipeline(name:String)

object EditingResolutionPipeline extends AmfResolutionPipeline("EDITING")
