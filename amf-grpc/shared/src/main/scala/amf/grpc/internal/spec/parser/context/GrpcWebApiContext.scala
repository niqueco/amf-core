package amf.grpc.internal.spec.parser.context

import amf.apicontract.client.scala.model.domain.security.SecurityScheme
import amf.apicontract.internal.spec.common.WebApiDeclarations
import amf.apicontract.internal.spec.common.emitter.SpecVersionFactory
import amf.apicontract.internal.spec.common.parser.{SecuritySchemeParser, WebApiContext}
import amf.core.client.scala.config.ParsingOptions
import amf.core.client.scala.model.domain.Shape
import amf.core.client.scala.parse.document.{ParsedReference, ParserContext}
import amf.core.internal.remote.{Grpc, Spec}
import amf.shapes.internal.spec.common.parser.{DontIgnoreCriteria, IgnoreCriteria, SpecSyntax}
import org.yaml.model.{YNode, YPart}

object GrpcVersionFactory extends SpecVersionFactory {
  override def securitySchemeParser: (YPart, SecurityScheme => SecurityScheme) => SecuritySchemeParser =
    throw new Exception("GRPC specs don't support security schemes")
}

class GrpcWebApiContext(
    override val loc: String,
    override val refs: Seq[ParsedReference],
    override val options: ParsingOptions,
    private val wrapped: ParserContext,
    private val ds: Option[WebApiDeclarations] = None,
    val messagePath: Seq[String] = Seq("")
) extends WebApiContext(loc, refs, options, wrapped, ds) {

  override def syntax: SpecSyntax = new SpecSyntax {
    override val nodes: Map[String, Set[String]] = Map()
  }
  override def spec: Spec = Grpc

  override def link(node: YNode): Either[String, YNode] =
    throw new Exception("GrpcContext cannot be used with a SYaml parser")

  override def ignoreCriteria: IgnoreCriteria = DontIgnoreCriteria

  override def autoGeneratedAnnotation(s: Shape): Unit = {}

  override val factory: SpecVersionFactory = GrpcVersionFactory

  def nestedMessage(messageName: String) =
    new GrpcWebApiContext(loc, refs, options, wrapped, ds, messagePath ++ Seq(messageName))

  def fullMessagePath(messageName: String): String = {
    if (messageName.startsWith(".")) { // fully qualified path
      messageName
    } else if (messageName.startsWith(messagePath(1))) { // reference from package
      "." + messageName
    } else { // relative to current path
      (messagePath ++ Seq(messageName)).mkString(".")
    }
  }

  def topLevelPackageRef(messageName: String): Option[String] = {
    if (messageName.startsWith(".")) { // fully qualified path
      None
    } else if (messageName.startsWith(messagePath(1))) { // reference from package
      Some("." + messageName)
    } else { // relative to current path
      Some((messagePath.take(2) ++ Seq(messageName)).mkString("."))
    }
  }
}
