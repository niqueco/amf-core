package amf.core.client.scala.parse.document

import amf.core.client.scala.model.document.{BaseUnit, ExternalFragment}
import org.yaml.model.YNode

case class ParsedReference(unit: BaseUnit, origin: Reference, ast: Option[YNode] = None) {
  def isExternalFragment: Boolean = unit.isInstanceOf[ExternalFragment]
}
