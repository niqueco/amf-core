package amf.core.internal.metamodel.domain

import amf.core.client.scala.model.domain.ObjectNode
import amf.core.client.scala.vocabulary.Namespace.Data
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.domain.common.DescribedElementModel
import amf.core.internal.metamodel.domain.federation.HasShapeFederationMetadataModel

trait ObjectNodeModel extends DomainElementModel with HasShapeFederationMetadataModel with DescribedElementModel {

  override def fields: List[Field]       = DataNodeModel.fields
  override val `type`: List[ValueType]   = Data + "Object" :: DataNodeModel.`type`
  override def modelInstance: ObjectNode = ObjectNode()

  override val doc: ModelDoc = ModelDoc(
    ModelVocabularies.Data,
    "ObjectNode",
    "Node that represents a dynamic object with records data structure"
  )
}

object ObjectNodeModel extends ObjectNodeModel
