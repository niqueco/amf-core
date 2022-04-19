package amf.core.internal.metamodel.domain.federation

import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.vocabulary.{Namespace, ValueType}
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.Array
import amf.core.internal.metamodel.domain.{DomainElementModel, ModelDoc, ModelVocabularies}

object EntityKeysModel extends DomainElementModel {

  val PrimaryKeys = Field(
      Array(EntityReferenceModel),
      Namespace.ApiFederation + "EntityPrimaryKeys",
      ModelDoc(ModelVocabularies.AmlDoc, "EntityPrimaryKey", "List of keys for this entity")
  )

  override def modelInstance: AmfObject = EntityKeys()

  override def fields: List[Field] = List(PrimaryKeys) ++ DomainElementModel.fields

  override val `type`: List[ValueType] = List(Namespace.ApiFederation + "Entity") ++ DomainElementModel.`type`

}
