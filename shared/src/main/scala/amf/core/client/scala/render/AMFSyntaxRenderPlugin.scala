package amf.core.client.scala.render

import amf.core.client.scala.parse.document.ParsedDocument
import amf.core.internal.plugins.AMFPlugin
import org.mulesoft.common.io.Output

trait AMFSyntaxRenderPlugin extends AMFPlugin[ParsedDocument] {

  def emit[W: Output](mediaType: String, ast: ParsedDocument, writer: W): Option[W]

  /** media types which specifies vendors that are parsed by this plugin.
    */
  def mediaTypes: Seq[String]

}
