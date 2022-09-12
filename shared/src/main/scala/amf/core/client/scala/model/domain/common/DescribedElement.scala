package amf.core.client.scala.model.domain.common

import amf.core.client.scala.model.StrField
import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.metamodel.domain.common.DescribedElementModel

trait DescribedElement extends AmfObject {
  override def meta: DescribedElementModel
  def description: StrField                 = fields.field(meta.Description)
  def withDescription(v: String): this.type = set(meta.Description, v)
}
