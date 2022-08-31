package amf.core.client.platform.model.domain

import amf.core.client.platform.model.StrField

/** All AmfObject supporting name
  */
trait NamedAmfObject {

  /** Return AmfObject name. */
  def name: StrField

  /** Update AmfObject name. */
  def withName(name: String): this.type

}
