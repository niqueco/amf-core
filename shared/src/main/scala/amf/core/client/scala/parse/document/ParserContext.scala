package amf.core.client.scala.parse.document

import amf.core.client.scala.config.ParsingOptions
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.parse.document
import amf.core.internal.parser.ParseConfiguration
import amf.core.internal.parser.domain.FutureDeclarations
import amf.core.internal.plugins.syntax.SYamlBasedErrorHandler
import amf.core.internal.validation.core.ValidationSpecification
import org.mulesoft.common.client.lexical.SourceLocation

import scala.collection.mutable

class SyamlBasedParserErrorHandler(
    override val rootContextDocument: String = "",
    override val refs: Seq[ParsedReference] = Seq.empty,
    override val futureDeclarations: FutureDeclarations = EmptyFutureDeclarations(),
    override val config: ParseConfiguration
) extends ParserContext(rootContextDocument, refs, futureDeclarations, config)
    with SYamlBasedErrorHandler

trait ErrorHandlingContext {

  implicit def eh: AMFErrorHandler

  def violation(violationId: ValidationSpecification, node: String, message: String)

  def violation(violationId: ValidationSpecification, node: AmfObject, message: String)

  def violation(specification: ValidationSpecification, node: String, message: String, loc: SourceLocation): Unit
}

trait UnresolvedComponents {
  def futureDeclarations: FutureDeclarations
  def eh: AMFErrorHandler
}

object EmptyFutureDeclarations {
  def apply(): FutureDeclarations = new FutureDeclarations {}
}

case class ParserContext(
    rootContextDocument: String = "",
    refs: Seq[ParsedReference] = Seq.empty,
    futureDeclarations: FutureDeclarations = EmptyFutureDeclarations(),
    config: ParseConfiguration
) extends ErrorHandlingContext
    with UnresolvedComponents {

  override def eh                           = config.eh
  var globalSpace: mutable.Map[String, Any] = mutable.Map()

  def forLocation(newLocation: String): ParserContext = {
    val copied: ParserContext = this.copy(rootContextDocument = newLocation)
    copied.globalSpace = globalSpace
    copied
  }

  private val sonsReferences: mutable.Map[String, BaseUnit] = mutable.Map()

  def addSonRef(ref: BaseUnit): this.type = this.synchronized {
    sonsReferences.get(ref.location().getOrElse(ref.id)) match {
      case Some(_) => // ignore
      case _ =>
        sonsReferences.put(ref.location().getOrElse(ref.id), ref)
    }
    this
  }

  private def getSonsParsedReferences: Seq[ParsedReference] =
    sonsReferences.values.map(u => document.ParsedReference(u, new Reference(u.location().getOrElse(u.id), Nil))).toSeq

  def copyWithSonsReferences(): ParserContext = {
    val context = this.copy(refs = this.refs ++ getSonsParsedReferences)
    context.globalSpace = this.globalSpace
    context
  }

  def violation(violationId: ValidationSpecification, node: String, message: String): Unit =
    eh.violation(violationId, node, message, rootContextDocument)

  override def violation(violationId: ValidationSpecification, node: AmfObject, message: String): Unit =
    eh.violation(violationId, node, message, rootContextDocument)

  def parsingOptions: ParsingOptions = config.parsingOptions

  override def violation(
      specification: ValidationSpecification,
      node: String,
      message: String,
      loc: SourceLocation
  ): Unit = eh.violation(specification, node, message, loc)
}
