package amf.core.internal.metamodel.domain.federation

import amf.core.client.scala.model.domain.federation.ShapeFederationMetadata
import amf.core.client.scala.vocabulary.Namespace.Federation
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.metamodel.domain.{ModelDoc, ModelVocabularies}

object ShapeFederationMetadataModel extends FederationMetadataModel {
  override def modelInstance: ShapeFederationMetadata = ShapeFederationMetadata()

  override val `type`: List[ValueType] = Federation + "ShapeFederationMetadata" :: FederationMetadataModel.`type`

  override val doc: ModelDoc = ModelDoc(
    ModelVocabularies.Federation,
    "ShapeFederationMetadata",
    "Model that contains data about how the Shape should be federated",
    superClasses = Seq((Federation + "FederationMetadata").iri())
  )
}
