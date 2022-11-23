package amf.core.internal.metamodel.domain.federation

import amf.core.client.scala.model.domain.federation.HasFederationMetadata
import amf.core.client.scala.vocabulary.Namespace.Federation
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.domain.{DomainElementModel, ModelDoc, ModelVocabularies}

trait HasFederationMetadataModel extends DomainElementModel {

  val FederationMetadata: Field = Field(
    ShapeFederationMetadataModel,
    Federation + "federationMetadata",
    ModelDoc(ModelVocabularies.Federation, "federationMetadata", "Metadata about how this Shape should be federated")
  )
}
