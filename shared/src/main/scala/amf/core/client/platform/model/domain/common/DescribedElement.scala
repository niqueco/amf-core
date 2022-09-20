package amf.core.client.platform.model.domain.common

import amf.core.client.platform.model.StrField
import amf.core.client.scala.model.domain.common.{DescribedElement => InternalHasDescription}
import amf.core.internal.convert.CoreClientConverters._

trait DescribedElement {
  private[amf] val _internal: InternalHasDescription

  def description: StrField = _internal.description

  def withDescription(description: String): this.type = {
    _internal.withDescription(description)
    this
  }

}
