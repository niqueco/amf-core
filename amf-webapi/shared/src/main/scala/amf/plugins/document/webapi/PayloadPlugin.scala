package amf.plugins.document.webapi

import amf.AmfProfile
import amf.client.plugins.{AMFDocumentPlugin, AMFPlugin}
import amf.core.client.ParsingOptions
import amf.core.model.document.{BaseUnit, PayloadFragment}
import amf.core.parser.{ParsedDocument, ParserContext, SimpleReferenceHandler, SyamlParsedDocument}
import amf.core.remote.{Payload, Platform}
import amf.core.resolution.pipelines.ResolutionPipeline
import amf.core.{Root, emitter}
import amf.plugins.document.webapi.contexts.PayloadContext
import amf.plugins.document.webapi.parser.PayloadParser
import amf.plugins.document.webapi.parser.spec.common.PayloadEmitter
import amf.plugins.document.webapi.resolution.pipelines.ValidationResolutionPipeline
import amf.plugins.domain.shapes.DataShapesDomainPlugin
import amf.plugins.domain.webapi.WebAPIDomainPlugin
import org.yaml.model.{YMap, YScalar}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PayloadPlugin extends AMFDocumentPlugin {

  override val ID: String = Payload.name

  val vendors = Seq(Payload.name)

  override def modelEntities: Nil.type = Nil

  override def serializableAnnotations(): Map[String, Nothing] = Map.empty

  override def dependencies() = Seq(WebAPIDomainPlugin, DataShapesDomainPlugin)

  // we are looking for documents with a very specific payload
  // otherwise, this plugin can become the fallback option.
  // Fallback option should be an external fragment.
  override def documentSyntaxes = Seq(
    "application/amf+json",
    "application/amf+yaml",
    "application/payload+json",
    "application/payload+yaml"
  )

  override def parse(root: Root,
                     parentContext: ParserContext,
                     platform: Platform,
                     options: ParsingOptions): Option[PayloadFragment] = {
    root.parsed match {
      case parsed: SyamlParsedDocument =>
        implicit val ctx: PayloadContext = new PayloadContext(root.location, parentContext.refs, parentContext)
        Some(PayloadParser(parsed.document, root.location, root.mediatype).parseUnit())
      case _ =>
        None
    }
  }

  override def canParse(root: Root): Boolean                   = notRAML(root) && notOAS(root) // any document can be parsed as a Payload
  override def referenceHandler(): SimpleReferenceHandler.type = SimpleReferenceHandler

  private def notRAML(root: Root) = root.parsed match {
    case parsed: SyamlParsedDocument => parsed.comment.isEmpty || !parsed.comment.get.metaText.startsWith("%")
    case _                           => false
  }

  private def notOAS(root: Root) = root.parsed match {
    case parsed: SyamlParsedDocument =>
      parsed.document.node.value match {
        case map: YMap =>
          !map.entries.exists(_.key.value.asInstanceOf[YScalar].text.startsWith("swagger"))
        case _ => true
      }
    case _ =>
      false
  }

  // Unparsing payloads not supported
  override def unparse(unit: BaseUnit, options: emitter.RenderOptions): Option[ParsedDocument] = unit match {
    case p: PayloadFragment => Some(SyamlParsedDocument(document = PayloadEmitter(p.encodes).emitDocument()))
    case _                  => None
  }

  override def canUnparse(unit: BaseUnit): Boolean = unit.isInstanceOf[PayloadFragment]

  /**
    * Resolves the provided base unit model, according to the semantics of the domain of the document
    */
  override def resolve(unit: BaseUnit, pipelineId: String = ResolutionPipeline.DEFAULT_PIPELINE): BaseUnit =
    new ValidationResolutionPipeline(AmfProfile, unit).resolve()

  override def init(): Future[AMFPlugin] = Future { this }

  /**
    * Does references in this type of documents be recursive?
    */
  override val allowRecursiveReferences: Boolean = false
}