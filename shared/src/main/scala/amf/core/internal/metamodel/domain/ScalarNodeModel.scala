package amf.core.internal.metamodel.domain

import amf.core.client.scala.model.domain.ScalarNode
import amf.core.client.scala.vocabulary.Namespace.Data
import amf.core.client.scala.vocabulary.{Namespace, ValueType}
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.{Iri, Str}
import amf.core.internal.metamodel.domain.common.DescribedElementModel
import amf.core.internal.metamodel.domain.federation.HasShapeFederationMetadataModel

object ScalarNodeModel extends DomainElementModel with HasShapeFederationMetadataModel with DescribedElementModel {

  val Value: Field =
    Field(Str, Namespace.Data + "value", ModelDoc(ModelVocabularies.Data, "value", "value for an scalar dynamic node"))

  val DataType: Field =
    Field(
      Iri,
      Namespace.Shacl + "datatype",
      ModelDoc(ModelVocabularies.Data, "dataType", "Data type of value for an scalar dynamic node")
    )

  override def fields: List[Field]       = Value :: DataType :: Description :: DataNodeModel.fields
  override val `type`: List[ValueType]   = Data + "Scalar" :: DataNodeModel.`type`
  override def modelInstance: ScalarNode = ScalarNode()

  override val doc: ModelDoc = ModelDoc(
    ModelVocabularies.Data,
    "ScalarNode",
    "Node that represents a dynamic scalar value data structure"
  )
}
