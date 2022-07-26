package amf.core.client.scala.model.domain.federation

import amf.core.client.scala.model.{BoolField, StrField}
import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.metamodel.domain.federation.FederationMetadataModel

trait FederationMetadata extends DomainElement {
  override def meta: FederationMetadataModel

  def name: StrField          = fields.field(meta.Name)
  def tags: Seq[StrField]     = fields.field(meta.Tags)
  def shareable: BoolField    = fields.field(meta.Shareable)
  def inaccessible: BoolField = fields.field(meta.Inaccessible)
  def overrideFrom: StrField  = fields.field(meta.OverrideFrom)

  def withName(name: String): this.type                  = set(meta.Name, name)
  def withTags(tags: Seq[String]): this.type             = set(meta.Tags, tags)
  def withShareable(shareable: Boolean): this.type       = set(meta.Shareable, shareable)
  def withInaccessible(inaccessible: Boolean): this.type = set(meta.Inaccessible, inaccessible)
  def withOverrideFrom(from: String): this.type          = set(meta.OverrideFrom, from)
}
