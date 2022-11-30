package amf.core.internal.metamodel.domain.federation

import amf.core.client.scala.model.domain.federation.HasFederationMetadata
import amf.core.client.scala.vocabulary.Namespace.Federation
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.domain.{DomainElementModel, ModelDoc, ModelVocabularies}

trait HasFederationMetadataModel extends DomainElementModel {

  protected val metadataModel: FederationMetadataModel

  // Has to be a def due to initialization errors. Could receive Model class as arg but it isn't a problem for now and fix is trivial
  def FederationMetadata: Field = Field(
      metadataModel,
      Federation + "federationMetadata",
      ModelDoc(
          ModelVocabularies.Federation,
          "federationMetadata",
          "Metadata about how this DomainElement should be federated"
      )
  )
}

trait HasShapeFederationMetadataModel extends HasFederationMetadataModel {
  override protected val metadataModel: ShapeFederationMetadataModel.type = ShapeFederationMetadataModel
}
