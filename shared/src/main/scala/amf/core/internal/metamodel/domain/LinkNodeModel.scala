package amf.core.internal.metamodel.domain

import amf.core.client.scala.model.domain.LinkNode
import amf.core.client.scala.vocabulary.Namespace.Data
import amf.core.client.scala.vocabulary.{Namespace, ValueType}
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.Str
import amf.core.internal.metamodel.domain.federation.HasShapeFederationMetadataModel

object LinkNodeModel extends DomainElementModel with HasShapeFederationMetadataModel {

  val Value: Field = Field(Str, Namespace.Data + "value", ModelDoc(ModelVocabularies.Data, "value"))
  val Alias: Field = Field(Str, Namespace.Data + "alias", ModelDoc(ModelVocabularies.Data, "alias"))

  override def fields: List[Field]     = List(Value) ++ DataNodeModel.fields
  override val `type`: List[ValueType] = Data + "Link" :: DataNodeModel.`type`
  override def modelInstance: LinkNode = LinkNode()

  override val doc: ModelDoc = ModelDoc(
    ModelVocabularies.Data,
    "LinkNode",
    "Node that represents a dynamic link in a data structure"
  )
}
