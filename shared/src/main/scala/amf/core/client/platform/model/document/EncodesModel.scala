package amf.core.client.platform.model.document

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.model.AmfObjectWrapper
import amf.core.client.platform.model.domain.DomainElement
import amf.core.client.scala.model.document.{EncodesModel => InternalEncodesModel}

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait EncodesModel extends AmfObjectWrapper {

  override private[amf] val _internal: InternalEncodesModel

  /** Encoded DomainElement described in the document element. */
  def encodes: DomainElement = _internal.encodes

  def withEncodes(encoded: DomainElement): this.type = {
    _internal.withEncodes(encoded)
    this
  }
}
