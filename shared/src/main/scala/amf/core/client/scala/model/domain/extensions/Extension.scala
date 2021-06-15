package amf.core.client.scala.model.domain.extensions

import amf.core.internal.metamodel.domain.extensions.DomainExtensionModel.Extension
import amf.core.client.scala.model.domain.{DataNode, DomainElement, Shape}

trait Extension extends DomainElement {
  def obtainSchema: Shape
  def extension: DataNode = fields.field(Extension)
}
