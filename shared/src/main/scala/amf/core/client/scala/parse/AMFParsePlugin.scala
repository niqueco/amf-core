package amf.core.client.scala.parse

import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.parse.document.{ParserContext, ReferenceHandler}
import amf.core.internal.parser.Root
import amf.core.internal.plugins.AMFPlugin

trait AMFParsePlugin extends AMFPlugin[Root] {

  def parse(document: Root, ctx: ParserContext): BaseUnit

  /**
    * media types which specifies vendors that are parsed by this plugin.
    */
  def mediaTypes: Seq[String]

  /**
    * media types which specifies vendors that may be referenced.
    */
  def validMediaTypesToReference: Seq[String]

  def referenceHandler(eh: AMFErrorHandler): ReferenceHandler

  // move to some vendor/dialect configuration?
  def allowRecursiveReferences: Boolean
}
