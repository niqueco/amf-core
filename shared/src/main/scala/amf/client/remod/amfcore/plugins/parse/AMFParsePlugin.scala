package amf.client.remod.amfcore.plugins.parse

import amf.client.remod.amfcore.plugins.AMFPlugin
import amf.core.Root
import amf.core.client.ParsingOptions
import amf.core.errorhandling.ErrorHandler
import amf.core.model.document.BaseUnit
import amf.core.parser.{ParserContext, ReferenceHandler}

trait AMFParsePlugin extends AMFPlugin[ParsingInfo] {

//  def parse(document:Root, ctx:ParserContext): BaseUnit // change parser for AMF context
  def parse(document: Root, ctx: ParserContext, options: ParsingOptions): BaseUnit

  def referenceHandler(eh:ErrorHandler): ReferenceHandler

  // move to some vendor/dialect configuration?
  def allowRecursiveReferences: Boolean

  // only used in AMFCompiler::verifyCrossReference for RAML validations
  val supportedVendors: Seq[String]
  val validVendorsToReference:Seq[String]

}



