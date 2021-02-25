package amf.client.`new`.amfcore.plugins

import amf.client.`new`.amfcore.{AMFParsePlugin, ParsingInfo, PluginPriority}
import amf.client.plugins.AMFDocumentPlugin
import amf.core.{CompilerContext, Root}
import amf.core.client.ParsingOptions
import amf.core.errorhandling.ErrorHandler
import amf.core.model.document.BaseUnit
import amf.core.parser.{LibraryReference, ParsedDocument, ParserContext, RefContainer, ReferenceHandler, ReferenceKind}
import amf.core.remote.{Platform, Vendor}
import org.yaml.model.YNode

case class AMFParsePluginAdapter(plugin: AMFDocumentPlugin) extends AMFParsePlugin {
  override def parse(document: Root, ctx: ParserContext, options: ParsingOptions): Option[BaseUnit] =
    plugin.parse(document, ctx, options)

  override val supportedVendors: Seq[String] = plugin.vendors
  override val validVendorsToReference: Seq[String] = plugin.validVendorsToReference.map(_.name)

  override def referenceHandler(eh: ErrorHandler): ReferenceHandler = plugin.referenceHandler(eh)

  override def allowRecursiveReferences: Boolean = plugin.allowRecursiveReferences

  override val id: String = "Parse " + plugin.ID

  override def applies(element: ParsingInfo): Boolean = {
    val syntaxCondition = element.vendor match {
      case Some(definedVendor) =>
        plugin.vendors.contains(definedVendor)
      case None =>
        plugin.documentSyntaxes.contains(element.parsed.mediatype)
    }
    syntaxCondition && plugin.canParse(element.parsed)
  }

  override def priority: PluginPriority = PluginPriority(plugin.priority)
}
