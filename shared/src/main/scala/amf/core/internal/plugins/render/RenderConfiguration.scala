package amf.core.internal.plugins.render

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.config.{AMFEventListener, RenderOptions}
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.render.AMFSyntaxRenderPlugin
import amf.core.client.scala.vocabulary.NamespaceAliases
import amf.core.internal.metamodel.Type
import amf.core.internal.registries.AMFRegistry

case class EmptyRenderConfiguration(eh: AMFErrorHandler, options: RenderOptions = RenderOptions())
    extends RenderConfiguration {
  override def renderPlugins: List[AMFRenderPlugin]            = Nil
  override def renderOptions: RenderOptions                    = options
  override def errorHandler: AMFErrorHandler                   = eh
  override def listeners: Set[AMFEventListener]                = Set.empty
  override def syntaxPlugin: List[AMFSyntaxRenderPlugin]       = Nil
  override def extensionModels: Map[String, Map[String, Type]] = Map.empty
  override def namespaceAliases: NamespaceAliases              = NamespaceAliases.apply()
  override def registry: AMFRegistry                           = AMFRegistry.empty

  def withRenderOptions(options: RenderOptions): RenderConfiguration = copy(options = options)
}

object RenderConfiguration {
  def empty(eh: AMFErrorHandler) = EmptyRenderConfiguration(eh)
}

trait RenderConfiguration {
  def renderPlugins: List[AMFRenderPlugin]
  def renderOptions: RenderOptions
  def errorHandler: AMFErrorHandler
  def listeners: Set[AMFEventListener]
  def syntaxPlugin: List[AMFSyntaxRenderPlugin]
  def extensionModels: Map[String, Map[String, Type]]
  def namespaceAliases: NamespaceAliases
  def registry: AMFRegistry

  def withRenderOptions(options: RenderOptions): RenderConfiguration
}

private[amf] case class DefaultRenderConfiguration(renderPlugins: List[AMFRenderPlugin],
                                                   syntaxPlugin: List[AMFSyntaxRenderPlugin],
                                                   renderOptions: RenderOptions,
                                                   errorHandler: AMFErrorHandler,
                                                   listeners: Set[AMFEventListener],
                                                   extensionModels: Map[String, Map[String, Type]],
                                                   namespaceAliases: NamespaceAliases,
                                                   registry: AMFRegistry)
    extends RenderConfiguration {
  def withRenderOptions(options: RenderOptions): RenderConfiguration = copy(renderOptions = options)
}

object DefaultRenderConfiguration {
  def apply(config: AMFGraphConfiguration): RenderConfiguration = {
    DefaultRenderConfiguration(
        config.registry.getPluginsRegistry.renderPlugins,
        config.registry.getPluginsRegistry.syntaxRenderPlugins,
        config.options.renderOptions,
        config.errorHandlerProvider.errorHandler(),
        config.listeners,
        config.registry.getEntitiesRegistry.extensionTypes,
        config.registry.getNamespaceAliases,
        config.registry
    )
  }
}
