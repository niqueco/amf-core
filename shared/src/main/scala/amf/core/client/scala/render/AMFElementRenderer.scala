package amf.core.client.scala.render

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.parse.document.ParsedDocument
import amf.core.internal.plugins.render.DefaultRenderConfiguration

object AMFElementRenderer {

  def renderElement(element: DomainElement, config: AMFGraphConfiguration): ParsedDocument = {
    val plugins      = config.registry.sortedElementRenderPlugins
    val errorHandler = config.errorHandlerProvider.errorHandler()
    val renderConfig = DefaultRenderConfiguration(config)
    plugins.find(_.applies(element, renderConfig)) match {
      case Some(plugin) => plugin.render(element, errorHandler, renderConfig)
      case None =>
        val ids = plugins.map(_.id)
        throw new Exception(s"Cannot serialize domain element '${element.id}' with plugins: $ids")
    }
  }
}
