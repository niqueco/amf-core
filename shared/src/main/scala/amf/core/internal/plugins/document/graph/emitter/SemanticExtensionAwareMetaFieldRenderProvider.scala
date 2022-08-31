package amf.core.internal.plugins.document.graph.emitter

import amf.core.client.scala.config.RenderOptions
import amf.core.internal.metamodel.Type

object SemanticExtensionAwareMetaFieldRenderProvider {
  def apply(
      extensions: Map[String, Map[String, Type]],
      options: RenderOptions
  ): SemanticExtensionAwareFieldRenderProvision = {
    new SemanticExtensionAwareFieldRenderProvision(createDomainToExtensionMap(extensions), options)
  }

  private def createDomainToExtensionMap(extensionModels: Map[String, Map[String, Type]]): Map[String, Set[String]] =
    extensionModels.map { case (domain: String, extensionToType: Map[String, Type]) =>
      domain -> extensionToType.keySet
    }
}
