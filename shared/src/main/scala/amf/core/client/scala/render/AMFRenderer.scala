package amf.core.client.scala.render

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.parse.document.ParsedDocument
import amf.core.internal.plugins.render.AMFGraphRenderPlugin
import amf.core.internal.remote.Vendor.AMF
import amf.core.internal.render.AMFSerializer
import org.yaml.builder.DocBuilder
object AMFRenderer {

  def render(bu: BaseUnit, configuration: AMFGraphConfiguration): String = render(bu, AMF.mediaType, configuration)

  def renderAST(bu: BaseUnit, configuration: AMFGraphConfiguration): ParsedDocument =
    renderAST(bu, AMF.mediaType, configuration)

  def render(bu: BaseUnit, mediaType: String, configuration: AMFGraphConfiguration): String =
    new AMFSerializer(bu, mediaType, configuration.renderConfiguration).renderToString

  def renderAST(bu: BaseUnit, mediaType: String, configuration: AMFGraphConfiguration): ParsedDocument =
    new AMFSerializer(bu, mediaType, configuration.renderConfiguration).renderAST

  def renderGraphToBuilder[T](bu: BaseUnit, builder: DocBuilder[T], conf: AMFGraphConfiguration): T = {
    // only plugin that currently supports rendering to builder interface
    AMFGraphRenderPlugin.emit(bu, builder, conf.renderConfiguration)
    builder.result
  }

}
