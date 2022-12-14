package amf.core.client.scala.model.domain.federation

import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.metamodel.domain.federation.HasFederationMetadataModel

trait HasFederationMetadata[T <: FederationMetadata] extends DomainElement {
  def meta: HasFederationMetadataModel
  def federationMetadata: T                          = fields.field(meta.FederationMetadata)
  def withFederationMetadata(metadata: T): this.type = set(meta.FederationMetadata, metadata)
}
