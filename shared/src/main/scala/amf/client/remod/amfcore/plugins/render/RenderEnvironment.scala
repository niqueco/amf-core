package amf.client.remod.amfcore.plugins.render

import amf.client.remod.BaseEnvironment
import amf.client.remod.amfcore.config.RenderOptions

trait RenderEnvironment {
  def renderPlugins: List[AMFRenderPlugin]
  def renderOptions: RenderOptions
}

case class DefaultRenderEnvironment(renderPlugins: List[AMFRenderPlugin], renderOptions: RenderOptions) extends RenderEnvironment

object DefaultRenderEnvironment {
  def apply(env: BaseEnvironment): RenderEnvironment = {
    DefaultRenderEnvironment(env.registry.plugins.renderPlugins, env.options.renderingOptions)
  }
}
