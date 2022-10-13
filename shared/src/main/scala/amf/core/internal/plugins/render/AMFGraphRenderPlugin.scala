package amf.core.internal.plugins.render

import amf.core.client.common.{LowPriority, PluginPriority}
import amf.core.client.scala.config.RenderOptions
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.vocabulary.NamespaceAliases
import amf.core.internal.plugins.document.graph.emitter.{
  ApplicableMetaFieldRenderProvider,
  EmbeddedJsonLdEmitter,
  FlattenedJsonLdEmitter,
  SemanticExtensionAwareMetaFieldRenderProvider
}
import amf.core.internal.plugins.document.graph.{EmbeddedForm, JsonLdSerialization}
import amf.core.internal.plugins.syntax.ASTBuilder
import amf.core.internal.remote.Amf
import amf.core.internal.remote.Mimes._
import org.yaml.builder.DocBuilder

object AMFGraphRenderPlugin extends AMFGraphRenderPlugin
trait AMFGraphRenderPlugin extends AMFRenderPlugin {

  override val id: String = Amf.id

  override def defaultSyntax(): String = `application/json`

  override def emit[T](
      unit: BaseUnit,
      builder: ASTBuilder[T],
      renderConfig: RenderConfiguration,
      mediaType: String
  ): Boolean = {
    builder match {
      case yDocBuilder: DocBuilder[_] =>
        emitToYDocBuilder(unit, yDocBuilder, renderConfig)
      case _ => false
    }
  }

  def flattenEmissionFN[T]
      : (BaseUnit, DocBuilder[T], RenderOptions, NamespaceAliases, ApplicableMetaFieldRenderProvider) => Boolean =
    FlattenedJsonLdEmitter.emit
  def emitToYDocBuilder[T](unit: BaseUnit, builder: DocBuilder[T], renderConfig: RenderConfiguration): Boolean = {
    val options          = renderConfig.renderOptions
    val namespaceAliases = renderConfig.namespaceAliases
    options.toGraphSerialization match {
      case JsonLdSerialization(EmbeddedForm) => EmbeddedJsonLdEmitter.emit(unit, builder, options, namespaceAliases)
      // defaults to flatten
      case _ =>
        flattenEmissionFN(
          unit,
          builder,
          options,
          namespaceAliases,
          SemanticExtensionAwareMetaFieldRenderProvider(renderConfig.extensionModels, options)
        )
    }
  }

  override def mediaTypes: Seq[String] = Seq(
    `application/ld+json`,
    `application/graph`
  )

  override def applies(element: RenderInfo): Boolean = true

  override def priority: PluginPriority = LowPriority

  override def getDefaultBuilder: ASTBuilder[_] = new SYAMLASTBuilder
}
