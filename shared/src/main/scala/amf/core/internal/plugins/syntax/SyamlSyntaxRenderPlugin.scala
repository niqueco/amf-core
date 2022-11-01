package amf.core.internal.plugins.syntax

import amf.core.client.common.{NormalPriority, PluginPriority}
import amf.core.client.scala.parse.document.{ParsedDocument, SyamlParsedDocument}
import amf.core.client.scala.render.AMFSyntaxRenderPlugin
import amf.core.internal.remote.Mimes._
import amf.core.internal.unsafe.PlatformSecrets
import org.mulesoft.common.io.Output
import org.yaml.model.YDocument
import org.yaml.render.{JsonRender, JsonRenderOptions, YamlRender}

object SyamlSyntaxRenderPlugin extends AMFSyntaxRenderPlugin with SyamlSyntaxBasePlugin with PlatformSecrets {

  override def emit[W: Output](mediaType: String, doc: ParsedDocument, writer: W): Option[W] = {
    doc match {
      case input: SyamlParsedDocument =>
        val ast = input.document
        render(mediaType, ast) { (format, ast) =>
          if (format == "yaml") YamlRender.render(writer, ast, expandReferences = false)
          else JsonRender.render(ast, writer, options = JsonRenderOptions().withoutNonAsciiEncode)
          Some(writer)
        }
      case _ => None
    }
  }

  private def render[T](mediaType: String, ast: YDocument)(render: (String, YDocument) => T): T = {
    val format = getFormat(mediaType)
    render(format, ast)
  }

  override def applies(element: ParsedDocument): Boolean = element.isInstanceOf[SyamlParsedDocument]

  override def mediaTypes: Seq[String] =
    Seq(
      `application/yaml`,
      `application/x-yaml`,
      `text/yaml`,
      `text/x-yaml`,
      `application/json`,
      `text/json`,
      `text/vnd.yaml`
    ) ++ graphSyntax

  override val id: String = "syaml-render"

  override def priority: PluginPriority = NormalPriority
}
