package amf.core.client.platform.model

import amf.core.client.scala.model.domain.{AmfObject => InternalAmfObject}
import amf.core.internal.convert.CoreClientConverters._

/** Base class for all the native wrappers
  */
trait AmfObjectWrapper extends Annotable {
  private[amf] val _internal: InternalAmfObject

  override def annotations(): Annotations = _internal.annotations
}
