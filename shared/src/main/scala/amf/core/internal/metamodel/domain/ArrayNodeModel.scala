package amf.core.internal.metamodel.domain

import amf.core.client.scala.model.domain.ArrayNode
import amf.core.client.scala.vocabulary.Namespace.Data
import amf.core.client.scala.vocabulary.{Namespace, ValueType}
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.Array
import amf.core.internal.metamodel.domain.federation.HasShapeFederationMetadataModel

object ArrayNodeModel extends DomainElementModel with HasShapeFederationMetadataModel {

  val Member: Field =
    Field(Array(DataNodeModel), Namespace.Rdfs + "member", ModelDoc(ExternalModelVocabularies.Rdf, "member", ""))

  override val fields: List[Field]      = Member :: DataNodeModel.fields
  override val `type`: List[ValueType]  = Data + "Array" :: Namespace.Rdf + "Seq" :: DataNodeModel.`type`
  override def modelInstance: ArrayNode = ArrayNode()

  override val doc: ModelDoc = ModelDoc(
    ModelVocabularies.Data,
    "ArrayNode",
    "Node that represents a dynamic array data structure"
  )
}
