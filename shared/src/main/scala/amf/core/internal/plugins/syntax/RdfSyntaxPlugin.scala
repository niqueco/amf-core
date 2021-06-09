package amf.core.internal.plugins.syntax

import amf.core.client.common.{LowPriority, PluginPriority}
import amf.core.client.scala.parse.AMFSyntaxParsePlugin
import amf.core.client.scala.parse.document.{ParsedDocument, ParserContext}
import amf.core.internal.rdf.RdfModelDocument
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.common.io.Output

object RdfSyntaxPlugin extends AMFSyntaxParsePlugin with PlatformSecrets {

  override val id = "Rdf"

  // TODO ARM to render syntax plugin
  def unparse[W: Output](mediaType: String, doc: ParsedDocument, writer: W): Option[W] =
    (doc, platform.rdfFramework) match {
      case (input: RdfModelDocument, Some(r)) => r.rdfModelToSyntaxWriter(mediaType, input, writer)
      case _                                  => None
    }
  override def parse(text: CharSequence, mediaType: String, ctx: ParserContext): ParsedDocument = {
    platform.rdfFramework match {
      case Some(r) if !ctx.parsingOptions.isAmfJsonLdSerialization => r.syntaxToRdfModel(mediaType, text)
      case _                                                       => throw new UnsupportedOperationException
    }
  }

  /**
    * media types which specifies vendors that are parsed by this plugin.
    */
  override def mediaTypes: Seq[String]                 = Nil
  override def applies(element: CharSequence): Boolean = true
  override def priority: PluginPriority                = LowPriority
}
