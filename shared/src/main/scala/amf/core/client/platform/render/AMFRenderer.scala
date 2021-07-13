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

  def render(baseUnit: BaseUnit, env: AMFGraphConfiguration): String = {
    implicit val executionContext: ExecutionContext = env.getExecutionContext
    InternalAMFRenderer.render(baseUnit, env)
  }

  def render(baseUnit: BaseUnit, mediaType: String, env: AMFGraphConfiguration): String = {
    implicit val executionContext: ExecutionContext = env.getExecutionContext
    InternalAMFRenderer.render(baseUnit, mediaType, env)
  }

  def renderGraphToBuilder[T](baseUnit: BaseUnit, builder: DocBuilder[T], config: AMFGraphConfiguration): T =
    InternalAMFRenderer.renderGraphToBuilder(baseUnit, builder, config)

}
