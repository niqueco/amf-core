package amf.core.client.scala.model.domain.federation

import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.metamodel.domain.federation.HasShapeFederationMetadataModel

trait HasShapeFederationMetadata extends DomainElement {
  def meta: HasShapeFederationMetadataModel
  def federationMetadata: ShapeFederationMetadata                          = fields.field(meta.FederationMetadata)
  def withFederationMetadata(metadata: ShapeFederationMetadata): this.type = set(meta.FederationMetadata, metadata)
}
