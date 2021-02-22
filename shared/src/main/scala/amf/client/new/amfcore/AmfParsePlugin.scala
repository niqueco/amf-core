package amf.client.`new`.amfcore

import amf.core.client.ParsingOptions
import amf.core.{CompilerContext, Root}
import amf.core.errorhandling.ErrorHandler
import amf.core.model.document.BaseUnit
import amf.core.parser.{ParsedDocument, ParserContext, RefContainer, ReferenceHandler, ReferenceKind}
import amf.core.remote.{Platform, Vendor}
import amf.core.validation.AMFValidationReport
import org.yaml.model.{YDocument, YNode}

import scala.concurrent.Future

sealed trait AmfPlugin[T] {
  val id: String
  def applies(element: T): Boolean
  // test for collisions?
  def priority: PluginPriority //?
}

object AmfPlugin {

  implicit def ordering[A <: AmfPlugin[_]]: Ordering[A] = (x: A, y: A) => {
    x.priority.priority compareTo (y.priority.priority)
  }

}



// YDocument will have to change, we need to use a container which is not attached to syaml
// Root is has a lot of information that is not used, can be limited to YDocument and raw string
case class ParsingInfo(parsed: Root, vendor: Option[String])

trait AmfParsePlugin extends AmfPlugin[ParsingInfo] {

//  def parse(document:Root, ctx:ParserContext): BaseUnit // change parser for AMF context
  def parse(document: Root, ctx: ParserContext, options: ParsingOptions): Option[BaseUnit]

  def referenceHandler(eh:ErrorHandler): ReferenceHandler

  // move to some vendor/dialect configuration?
  def allowRecursiveReferences: Boolean

  // ideally can deleted, only used in AMFCompiler::verifyCrossReference for RAML validations
  val supportedVendors: Seq[String]
  val validVendorsToReference:Seq[String]

  // ideally can deleted, only used in Reference::resolveReference for RAML validations
  def verifyReferenceKind(unit: BaseUnit,
                          definedKind: ReferenceKind,
                          allKinds: Seq[ReferenceKind],
                          nodes: Seq[YNode],
                          ctx: ParserContext): Unit = Unit
  // ideally can deleted, only used in Reference::parseReferences for RAML validations
  def verifyValidFragment(refVendor: Option[Vendor], refs: Seq[RefContainer], ctx: CompilerContext): Unit = Unit

}

trait AmfValidatePlugin extends AmfPlugin[BaseUnit] {
  def validate: AMFValidationReport
}

sealed case class PluginPriority(priority: Int) {}

object HighPriority extends PluginPriority(1)

object NormalPriority extends PluginPriority(2)

object LowPriority extends PluginPriority(3)
