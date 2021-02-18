package amf.client.`new`.amfcore.plugins

import amf.client.`new`.amfcore.{AmfParsePlugin, ParsingInfo, PluginPriority}
import amf.client.plugins.AMFDocumentPlugin
import amf.core.{CompilerContext, Root}
import amf.core.client.ParsingOptions
import amf.core.errorhandling.ErrorHandler
import amf.core.model.document.BaseUnit
import amf.core.parser.{LibraryReference, ParsedDocument, ParserContext, RefContainer, ReferenceHandler, ReferenceKind}
import amf.core.remote.{Platform, Vendor}
import org.yaml.model.YNode

case class AmfParsePluginAdapter (plugin: AMFDocumentPlugin) extends AmfParsePlugin {
  override def parse(document: Root, ctx: ParserContext, options: ParsingOptions): Option[BaseUnit] =
    plugin.parse(document, ctx, options)

  override val supportedVendors: Seq[Vendor] = plugin.vendors.map(Vendor(_))
  override val validVendorsToReference: Seq[Vendor] = plugin.validVendorsToReference

  override def referenceHandler(eh: ErrorHandler): ReferenceHandler = plugin.referenceHandler(eh)


  override def verifyReferenceKind(unit: BaseUnit, definedKind: ReferenceKind, allKinds: Seq[ReferenceKind], nodes: Seq[YNode], ctx: ParserContext): Unit =
    plugin.verifyReferenceKind(unit, definedKind, allKinds, nodes, ctx)

  override def verifyValidFragment(refVendor: Option[Vendor], refs: Seq[RefContainer], ctx: CompilerContext): Unit =
    plugin.verifyValidFragment(refVendor, refs, ctx)

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
