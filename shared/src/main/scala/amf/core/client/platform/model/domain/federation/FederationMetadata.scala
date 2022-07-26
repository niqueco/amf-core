package amf.core.client.platform.model.domain.federation

import amf.core.client.platform.model.{BoolField, StrField}
import amf.core.client.platform.model.domain.DomainElement
import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.scala.model.domain.federation.{FederationMetadata => InternalFederationMetadata}

trait FederationMetadata extends DomainElement {
  override private[amf] val _internal: InternalFederationMetadata

  def name: StrField             = _internal.name
  def tags: ClientList[StrField] = _internal.tags.asClient
  def shareable: BoolField       = _internal.shareable
  def inaccessible: BoolField    = _internal.inaccessible
  def overrideFrom: StrField     = _internal.overrideFrom

  def withName(name: String): this.type = {
    _internal.withName(name)
    this
  }
  def withTags(tags: ClientList[String]): this.type = {
    _internal.withTags(tags.asInternal)
    this
  }
  def withShareable(shareable: Boolean): this.type = {
    _internal.withShareable(shareable)
    this
  }
  def withInaccessible(inaccessible: Boolean): this.type = {
    _internal.withInaccessible(inaccessible)
    this
  }
  def withOverride(from: String): this.type = {
    _internal.withOverrideFrom(from)
    this
  }
}
