package amf.client.remod.amfcore.plugins.render

import amf.client.remod.BaseEnvironment
import amf.client.remod.amfcore.config.RenderOptions
import amf.core.errorhandling.ErrorHandler

trait RenderEnvironment {
  def renderPlugins: List[AMFRenderPlugin]
  def renderOptions: RenderOptions
  def errorHandler: ErrorHandler
}

case class DefaultRenderEnvironment(renderPlugins: List[AMFRenderPlugin],
                                    renderOptions: RenderOptions,
                                    errorHandler: ErrorHandler)
    extends RenderEnvironment

object DefaultRenderEnvironment {
  def apply(env: BaseEnvironment): RenderEnvironment = {
    DefaultRenderEnvironment(env.registry.plugins.renderPlugins,
                             env.options.renderOptions,
                             env.errorHandlerProvider.errorHandler())
  }
}
