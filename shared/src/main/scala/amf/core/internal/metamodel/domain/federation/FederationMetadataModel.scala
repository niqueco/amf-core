package amf.core.internal.metamodel.domain.federation

import amf.core.client.scala.vocabulary.Namespace.Federation
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.{Array, Bool, Str}
import amf.core.internal.metamodel.domain.{DomainElementModel, ModelDoc, ModelVocabularies}
import amf.core.client.scala.model.domain.federation.FederationMetadata

trait FederationMetadataModel extends DomainElementModel {

  val Name: Field = Field(
    Str,
    Federation + "name",
    ModelDoc(ModelVocabularies.Federation, "name", "Name element in the federated graph")
  )

  val Tags: Field =
    Field(
      Array(Str),
      Federation + "tags",
      ModelDoc(ModelVocabularies.Federation, "tags", "Federation tags of the element")
    )

  val Shareable: Field =
    Field(
      Bool,
      Federation + "shareable",
      ModelDoc(
        ModelVocabularies.Federation,
        "shareable",
        "Element can be defined by more than one component of the federated graph"
      )
    )

  val Inaccessible: Field = Field(
    Bool,
    Federation + "inaccessible",
    ModelDoc(ModelVocabularies.Federation, "inaccessible", "Element cannot be accessed by the federated graph")
  )

  val OverrideFrom: Field =
    Field(
      Str,
      Federation + "overrideFrom",
      ModelDoc(
        ModelVocabularies.Federation,
        "override",
        "Indicates that the current subgraph is taking responsibility for resolving the marked field away from the subgraph specified in the from argument"
      )
    )

  override def modelInstance: FederationMetadata

  override val `type`: List[ValueType] = Federation + "FederationMetadata" :: DomainElementModel.`type`

  override val fields: List[Field] =
    Name :: Tags :: Shareable :: Inaccessible :: OverrideFrom :: DomainElementModel.fields

  override val doc: ModelDoc = ModelDoc(
    ModelVocabularies.Federation,
    "FederationMetadata",
    "Model that contains data about how the element should be federated"
  )
}

object FederationMetadataModel extends FederationMetadataModel {
  override def modelInstance: FederationMetadata = throw new Exception("FederationMetadataModel is an abstract class")
}
