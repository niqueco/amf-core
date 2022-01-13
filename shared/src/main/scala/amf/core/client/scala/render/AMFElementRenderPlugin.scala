package amf.core.client.scala.render

import amf.core.client.common.{NormalPriority, PluginPriority}
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.parse.document.ParsedDocument
import amf.core.internal.plugins.AMFPlugin
import amf.core.internal.plugins.render.RenderConfiguration
import org.yaml.builder.DocBuilder

case class ElementRenderRequest(element: DomainElement, config: RenderConfiguration)

trait AMFElementRenderPlugin extends AMFPlugin[ElementRenderRequest] {

  override def applies(request: ElementRenderRequest): Boolean = applies(request.element, request.config)
  def applies(element: DomainElement, config: RenderConfiguration): Boolean

  def render(element: DomainElement,
             errorHandler: AMFErrorHandler,
             renderConfiguration: RenderConfiguration): ParsedDocument

  override def priority: PluginPriority = NormalPriority
}
