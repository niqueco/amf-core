package amf.client.`new`.amfcore

import amf.core.Root
import amf.core.errorhandling.ErrorHandler
import amf.core.model.document.BaseUnit
import amf.core.parser.{ParserContext, ReferenceHandler}
import amf.core.remote.Vendor
import amf.core.validation.AMFValidationReport
import org.yaml.model.YDocument

import scala.concurrent.Future

sealed trait AmfPlugin[T] extends Ordering[AmfPlugin[_]] {
  val id: String
  def applies(element: T): Boolean
  // test for collisions?
  def priority: PluginPriority //?

  override def compare(x: AmfPlugin[_], y: AmfPlugin[_]): Int = {
    x.priority.priority compareTo (y.priority.priority)
  }
}

trait AmfParsePlugin extends AmfPlugin[YDocument] {

  def parse(document:Root, ctx:ParserContext): BaseUnit // change parser for AMF context
  val supportedVendors: Seq[Vendor]
  val validVendorsToReference:Seq[Vendor]

  def apply(element: YDocument, vendor: Vendor) = supportedVendors.contains(vendor) && apply(element)

  def referenceHandler(eh:ErrorHandler): ReferenceHandler

  // move to some vendor/dialect configuration?
  def allowRecursiveReferences:Boolean

}

trait AmfValidatePlugin extends AmfPlugin[BaseUnit] {
  def validate: AMFValidationReport
}

sealed case class PluginPriority(priority: Int) {}

object HighPriority extends PluginPriority(1)

object NormalPriority extends PluginPriority(2)

object LowPriority extends PluginPriority(3)
