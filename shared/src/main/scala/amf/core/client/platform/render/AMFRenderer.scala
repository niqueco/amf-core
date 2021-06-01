package amf.core.client.platform.render

import amf.core.client.platform.AMFGraphConfiguration
import amf.core.client.platform.model.document.BaseUnit
import amf.core.client.scala.render.{AMFRenderer => InternalAMFRenderer}
import amf.core.internal.convert.CoreClientConverters._
import scala.concurrent.ExecutionContext
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import org.yaml.builder.DocBuilder

@JSExportAll
@JSExportTopLevel("AMFRenderer")
object AMFRenderer {

  def render(bu: BaseUnit, env: AMFGraphConfiguration): String = {
    implicit val executionContext: ExecutionContext = env.getExecutionContext
    InternalAMFRenderer.render(bu, env)
  }

  def render(bu: BaseUnit, mediaType: String, env: AMFGraphConfiguration): String = {
    implicit val executionContext: ExecutionContext = env.getExecutionContext
    InternalAMFRenderer.render(bu, mediaType, env)
  }

  def renderGraphToBuilder[T](bu: BaseUnit, builder: DocBuilder[T], config: AMFGraphConfiguration): T =
    InternalAMFRenderer.renderGraphToBuilder(bu, builder, config)

}
