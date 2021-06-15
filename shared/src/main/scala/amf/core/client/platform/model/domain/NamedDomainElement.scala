package amf.core.client.platform.model.domain

import amf.core.client.platform.model.StrField

/**
  * All DomainElements supporting name
  */
trait NamedDomainElement {

  /** Return DomainElement name. */
  def name: StrField

  /** Update DomainElement name. */
  def withName(name: String): this.type

}
