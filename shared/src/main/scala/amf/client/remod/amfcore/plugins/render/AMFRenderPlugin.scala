package amf.client.remod.amfcore.plugins.render

import amf.client.plugins.AMFDocumentPlugin
import amf.client.remod.amfcore.config.RenderOptions
import amf.core.emitter.{RenderOptions => LegacyRenderOptions}
import amf.core.emitter.{ShapeRenderOptions => LegacyShapeRenderOptions}
import amf.client.remod.amfcore.plugins.{AMFPlugin, PluginPriority}
import amf.core.model.document.BaseUnit
import org.yaml.builder.DocBuilder

private[amf] trait AMFRenderPlugin extends AMFPlugin[RenderInfo] {
  def emit[T](unit: BaseUnit, builder: DocBuilder[T], renderOptions: RenderOptions = RenderOptions()): Boolean
}

private[amf] case class AMFRenderPluginAdapter(plugin: AMFDocumentPlugin) extends AMFRenderPlugin {

  override def emit[T](unit: BaseUnit, builder: DocBuilder[T], renderOptions: RenderOptions): Boolean =
    plugin.emit(unit,
                builder,
                LegacyRenderOptions.fromImmutable(renderOptions),
                LegacyShapeRenderOptions.fromImmutable(renderOptions.shapeRenderOptions))

  override val id: String = plugin.ID

  override def applies(renderingInfo: RenderInfo): Boolean = {
    plugin.vendors.contains(renderingInfo.vendor) &&
    plugin.documentSyntaxes.contains(renderingInfo.mediaType) &&
    plugin.canUnparse(renderingInfo.unit)
  }

  override def priority: PluginPriority = PluginPriority(plugin.priority)
}
