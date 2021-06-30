package amf.core.internal.plugins.render

import amf.core.client.platform.config.AMFLogger
import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.config.{AMFEventListener, RenderOptions}
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.render.AMFSyntaxRenderPlugin
import amf.core.internal.plugins.namespace.NamespaceAliasesPlugin

trait RenderConfiguration {
  def renderPlugins: List[AMFRenderPlugin]
  def namespacePlugins: List[NamespaceAliasesPlugin]
  def renderOptions: RenderOptions
  def errorHandler: AMFErrorHandler
  def listeners: Set[AMFEventListener]
  def syntaxPlugin: List[AMFSyntaxRenderPlugin]
  def logger: AMFLogger
}

private[amf] case class DefaultRenderConfiguration(renderPlugins: List[AMFRenderPlugin],
                                                   syntaxPlugin: List[AMFSyntaxRenderPlugin],
                                                   namespacePlugins: List[NamespaceAliasesPlugin],
                                                   renderOptions: RenderOptions,
                                                   errorHandler: AMFErrorHandler,
                                                   listeners: Set[AMFEventListener],
                                                   logger: AMFLogger)
    extends RenderConfiguration {}

object DefaultRenderConfiguration {
  def apply(config: AMFGraphConfiguration): RenderConfiguration = {
    DefaultRenderConfiguration(
        config.registry.plugins.renderPlugins,
        config.registry.plugins.syntaxRenderPlugins,
        config.registry.plugins.namespacePlugins,
        config.options.renderOptions,
        config.errorHandlerProvider.errorHandler(),
        config.listeners,
        config.logger
    )
  }
}
