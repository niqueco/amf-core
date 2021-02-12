package amf.client.`new`.amfcore.plugins

import amf.client.`new`.amfcore.{AmfParsePlugin, PluginPriority}
import amf.core.Root
import amf.core.client.ParsingOptions
import amf.core.errorhandling.ErrorHandler
import amf.core.model.document.BaseUnit
import amf.core.parser.{ParsedDocument, ParserContext, ReferenceHandler}
import amf.core.remote.{Platform, Vendor}
import org.yaml.model.YDocument

// default plugins options
//object GuessingParsePlugin extends AmfParsePlugin {
//  override val supportedMediatypes: Seq[String] = ???
//
//  override def parse(document: Root, ctx: ParserContext, platform: Platform, options: ParsingOptions): Option[BaseUnit] = ???
//
//  override val supportedVendors: Seq[Vendor] = ???
//  override val validVendorsToReference: Seq[Vendor] = ???
//
//  override def referenceHandler(eh: ErrorHandler): ReferenceHandler = ???
//
//  override def allowRecursiveReferences: Boolean = ???
//
//  override val id: String = ???
//
//  override def applies(element: ParsedDocument): Boolean = ???
//
//  override def priority: PluginPriority = ???
//}

//object ExternalFragmentParsePlugin extends AmfParsePlugin {
//
//  override val supportedMediatypes: Seq[String] = ???
//
//  override def parse(document: Root, ctx: ParserContext, platform: Platform, options: ParsingOptions): Option[BaseUnit] = ???
//
//  override val supportedVendors: Seq[Vendor] = ???
//  override val validVendorsToReference: Seq[Vendor] = ???
//
//  override def referenceHandler(eh: ErrorHandler): ReferenceHandler = ???
//
//  override def allowRecursiveReferences: Boolean = ???
//
//  override val id: String = ???
//
//  override def applies(element: ParsedDocument): Boolean = ???
//
//  override def priority: PluginPriority = ???
//}
