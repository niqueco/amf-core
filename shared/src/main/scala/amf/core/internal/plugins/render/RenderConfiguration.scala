package amf.core.internal.plugins.render

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.config.{AMFEventListener, RenderOptions}
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.render.AMFSyntaxRenderPlugin
import amf.core.client.scala.vocabulary.NamespaceAliases
import amf.core.internal.metamodel.Type

trait RenderConfiguration {
  def renderPlugins: List[AMFRenderPlugin]
  def renderOptions: RenderOptions
  def errorHandler: AMFErrorHandler
  def listeners: Set[AMFEventListener]
  def syntaxPlugin: List[AMFSyntaxRenderPlugin]
  def extensionModels: Map[String, Map[String, Type]]
  def namespaceAliases: NamespaceAliases
}

private[amf] case class DefaultRenderConfiguration(renderPlugins: List[AMFRenderPlugin],
                                                   syntaxPlugin: List[AMFSyntaxRenderPlugin],
                                                   renderOptions: RenderOptions,
                                                   errorHandler: AMFErrorHandler,
                                                   listeners: Set[AMFEventListener],
                                                   extensionModels: Map[String, Map[String, Type]],
                                                   namespaceAliases: NamespaceAliases)
    extends RenderConfiguration {}

object DefaultRenderConfiguration {
  def apply(config: AMFGraphConfiguration): RenderConfiguration = {
    DefaultRenderConfiguration(
        config.registry.getPluginsRegistry.renderPlugins,
        config.registry.getPluginsRegistry.syntaxRenderPlugins,
        config.options.renderOptions,
        config.errorHandlerProvider.errorHandler(),
        config.listeners,
        config.registry.getEntitiesRegistry.extensionTypes,
        config.registry.getNamespaceAliases
    )
  }
}
