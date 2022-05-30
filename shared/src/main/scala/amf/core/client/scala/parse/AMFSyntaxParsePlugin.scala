package amf.core.client.scala.parse

import amf.core.client.scala.parse.document.{ParsedDocument, ParserContext}
import amf.core.internal.plugins.AMFPlugin

trait AMFSyntaxParsePlugin extends AMFPlugin[CharSequence] {

  def parse(text: CharSequence, mediaType: String, ctx: ParserContext): ParsedDocument

  /** media types which specifies vendors that are parsed by this plugin.
    */
  def mediaTypes: Seq[String]

  def mainMediaType: String
}
