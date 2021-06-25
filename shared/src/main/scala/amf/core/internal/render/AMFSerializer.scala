package amf.core.internal.render

import amf.core.client.scala.config._
import amf.core.client.scala.model.document.{BaseUnit, ExternalFragment}
import amf.core.client.scala.parse.document.{ParsedDocument, StringParsedDocument, SyamlParsedDocument}
import amf.core.internal.plugins.document.graph._
import amf.core.internal.plugins.render.{AMFGraphRenderPlugin, AMFRenderPlugin, RenderConfiguration, RenderInfo}
import amf.core.internal.plugins.syntax.{RdfSyntaxPlugin, StringDocBuilder}
import amf.core.internal.rdf.RdfModelDocument
import amf.core.internal.remote.{MediaTypeParser, Platform, Vendor}
import amf.core.internal.unsafe.PlatformSecrets
import amf.core.internal.validation.CoreValidations
import org.mulesoft.common.io.Output
import org.mulesoft.common.io.Output._
import org.yaml.builder.{JsonOutputBuilder, YDocumentBuilder}
import org.yaml.model.{YDocument, YNode}

import java.io.StringWriter
import scala.concurrent.{ExecutionContext, Future}

class AMFSerializer(unit: BaseUnit, mediaType: String, config: RenderConfiguration) extends PlatformSecrets {

  private val mediaTypeExp = new MediaTypeParser(mediaType)

  private val options = config.renderOptions

  def renderAsYDocument(renderPlugin: AMFRenderPlugin): SyamlParsedDocument = {

    val builder = new YDocumentBuilder
    notifyEvent(StartingRenderingEvent(unit, renderPlugin, mediaType))
    if (renderPlugin.emit(unit, builder, config)) {
      val result = SyamlParsedDocument(builder.result.asInstanceOf[YDocument])
      notifyEvent(FinishedRenderingASTEvent(unit, result))
      result
    } else throw new Exception(s"Error unparsing syntax $mediaType with domain plugin ${renderPlugin.id}")
  }

  private def notifyEvent(e: AMFEvent): Unit = config.listeners.foreach(_.notifyEvent(e))

  /** Print ast to writer. */
  def renderToWriter[W: Output](writer: W)(implicit executor: ExecutionContext): Unit = render(writer)

  /** Print ast to string. */
  def renderToString: String = render()

  /** Print ast to file. */
  def renderToFile(remote: Platform, path: String)(implicit executor: ExecutionContext): Future[Unit] =
    remote.write(path, renderToString)

  private def render[W: Output](writer: W): Unit = {
    notifyEvent(StartingRenderToWriterEvent(unit, mediaType))
    mediaTypeExp.getPureVendorExp match {
      case Vendor.AMF.mediaType =>
        config.renderOptions.toGraphSerialization match {
          case RdfSerialization()     => emitRdf(writer)
          case JsonLdSerialization(_) => emitJsonldToWriter(writer)
        }
      case Vendor.PROTO3.mediaType| Vendor.GRPC.mediaType =>
        val renderPlugin = getRenderPlugin
        val rendered     = renderAsString(renderPlugin)
        writer.append(rendered.ast.builder)
        notifyEvent(FinishedRenderingSyntaxEvent(unit))
      case _ =>
        val renderPlugin = getRenderPlugin
        val ast          = renderAsYDocument(renderPlugin)
        val mT           = mediaTypeExp.getSyntaxExp.getOrElse(renderPlugin.defaultSyntax())
        getSyntaxPlugin(ast, mT) match {
          case Some(syntaxPlugin) =>
            syntaxPlugin.emit(mT, ast, writer)
            notifyEvent(FinishedRenderingSyntaxEvent(unit))
          case None if unit.isInstanceOf[ExternalFragment] =>
            writer.append(unit.asInstanceOf[ExternalFragment].encodes.raw.value())
          case _ => throw new Exception(s"Unsupported media type $mediaType")
        }
    }
  }

  def renderAST: ParsedDocument = {
    mediaTypeExp.getPureVendorExp match {
      case Vendor.AMF.mediaType if config.renderOptions.toGraphSerialization.isInstanceOf[RdfSerialization] =>
        toRdfModelDoc.getOrElse(SyamlParsedDocument(YDocument(YNode.Empty)))
      case _ => renderYDocumentWithPlugins
    }
  }

  private[amf] def renderYDocumentWithPlugins: SyamlParsedDocument = {
    val renderPlugin = getRenderPlugin
    renderAsYDocument(renderPlugin)
  }

  def renderAsString(renderPlugin: AMFRenderPlugin): StringParsedDocument = {

    val builder = new StringDocBuilder()
    notifyEvent(StartingRenderingEvent(unit, renderPlugin, mediaType))
    if (renderPlugin.emitString(unit, builder, config)) {
      val result = builder.result
      notifyEvent(FinishedRenderingASTEvent(unit, result))
      result
    } else throw new Exception(s"Error unparsing syntax $mediaType with domain plugin ${renderPlugin.id}")
  }


  private def getSyntaxPlugin(ast: SyamlParsedDocument, mediaType: String) = {
    val candidates = config.syntaxPlugin.filter(_.mediaTypes.contains(mediaType))
    candidates.find(_.applies(ast))
  }

  private def emitJsonldToWriter[W: Output](writer: W): Unit = {
    val b = JsonOutputBuilder[W](writer, options.isPrettyPrint)
    AMFGraphRenderPlugin.emit(unit, b, config)
  }

  private def emitRdf[W: Output](writer: W): Unit =
    toRdfModelDoc.foreach(RdfSyntaxPlugin.unparse(mediaType, _, writer))

  private def toRdfModelDoc: Option[RdfModelDocument] = {
    platform.rdfFramework match {
      case Some(r) => Some(RdfModelDocument(r.unitToRdfModel(unit, config.renderOptions)))
      case _ =>
        config.errorHandler
          .violation(CoreValidations.UnableToParseRdfDocument, unit.id, "Unable to generate RDF Model")
        None
    }
  }

  private[amf] def render(): String = {
    val w = new StringWriter
    render(w)
    w.toString
  }

  private[amf] def getRenderPlugin: AMFRenderPlugin = {
    val renderPlugin =
      config.renderPlugins.filter(_.mediaTypes.contains(mediaType)).sorted.find(_.applies(RenderInfo(unit, mediaType)))
    renderPlugin.getOrElse {
      throw new Exception(s"Cannot serialize domain model '${unit.location()}' for media type $mediaType")
    }
  }
}
