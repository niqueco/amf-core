package amf.client.remod.amfcore.plugins.render

import amf.client.plugins.AMFDocumentPlugin
import amf.client.remod.amfcore.plugins.{AMFPlugin, PluginPriority}
import amf.core.emitter.{RenderOptions, ShapeRenderOptions}
import amf.core.model.document.BaseUnit
import org.yaml.builder.DocBuilder

private[amf] trait AMFRenderPlugin extends AMFPlugin[RenderInfo] {
  def emit[T](unit: BaseUnit,
              builder: DocBuilder[T],
              renderOptions: RenderOptions = RenderOptions(),
              shapeRenderOptions: ShapeRenderOptions = ShapeRenderOptions()): Boolean
}


private[amf] case class AMFRenderPluginAdapter(plugin: AMFDocumentPlugin) extends AMFRenderPlugin {

  override def emit[T](unit: BaseUnit, builder: DocBuilder[T], renderOptions: RenderOptions, shapeRenderOptions: ShapeRenderOptions): Boolean =
    plugin.emit(unit, builder, renderOptions, shapeRenderOptions)

  override val id: String = plugin.ID

  override def applies(renderingInfo: RenderInfo): Boolean = {
    plugin.vendors.contains(renderingInfo.vendor) &&
      plugin.documentSyntaxes.contains(renderingInfo.mediaType) &&
      plugin.canUnparse(renderingInfo.unit)
  }

  override def priority: PluginPriority = PluginPriority(plugin.priority)
}

