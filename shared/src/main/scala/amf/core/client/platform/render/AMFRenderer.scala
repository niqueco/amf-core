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

  def render(baseUnit: BaseUnit, configuration: AMFGraphConfiguration): String = {
    implicit val executionContext: ExecutionContext = configuration.getExecutionContext
    InternalAMFRenderer.render(baseUnit, configuration)
  }

  def render(baseUnit: BaseUnit, mediaType: String, configuration: AMFGraphConfiguration): String = {
    implicit val executionContext: ExecutionContext = configuration.getExecutionContext
    InternalAMFRenderer.render(baseUnit, mediaType, configuration)
  }

  def renderGraphToBuilder[T](baseUnit: BaseUnit, builder: DocBuilder[T], configuration: AMFGraphConfiguration): T =
    InternalAMFRenderer.renderGraphToBuilder(baseUnit, builder, configuration)

}
