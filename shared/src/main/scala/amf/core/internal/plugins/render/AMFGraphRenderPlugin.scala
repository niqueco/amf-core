package amf.core.internal.plugins.render

import amf.core.client.common.{NormalPriority, PluginPriority}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.remote.Amf
import amf.core.client.scala.vocabulary.Namespace
import amf.core.internal.plugins.document.graph.emitter.EmbeddedJsonLdEmitter
import amf.core.internal.plugins.render.AMFRenderPlugin.APPLICATION_JSON
import org.yaml.builder.DocBuilder

object AMFGraphRenderPlugin extends AMFRenderPlugin {

  override val id: String = Amf.name

  override def defaultSyntax(): String = APPLICATION_JSON

  override def emit[T](unit: BaseUnit, builder: DocBuilder[T], renderConfiguration: RenderConfiguration): Boolean = {
    val namespaceAliases = renderConfiguration.namespacePlugins.sorted
      .find(_.applies(unit))
      .map(_.aliases(unit))
      .getOrElse(Namespace.defaultAliases)
    EmbeddedJsonLdEmitter.emit(unit, builder, renderConfiguration.renderOptions, namespaceAliases)
  }

  override def mediaTypes: Seq[String] = Seq(
      Amf.mediaType,
      "application/ld+json",
      "application/json",
      "application/amf+json",
      "application/amf+json",
      "application/graph"
  )

  override def applies(element: RenderInfo): Boolean = true

  override def priority: PluginPriority = NormalPriority
}
