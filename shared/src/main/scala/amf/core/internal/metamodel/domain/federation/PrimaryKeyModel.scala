package amf.core.internal.metamodel.domain.federation

import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.vocabulary.{Namespace, ValueType}
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.{Array, Str}
import amf.core.internal.metamodel.domain.{DomainElementModel, ModelDoc, ModelVocabularies}

object PrimaryKeyModel extends DomainElementModel {

  val Fields = Field(
      Array(Str),
      Namespace.ApiFederation + "entityReferenceFieldName",
      ModelDoc(ModelVocabularies.ApiFederation,
               "entityReferenceFieldName",
               "List of field names that compose this key")
  )

  override def modelInstance: AmfObject = Key()

  override def fields: List[Field] = List(Fields) ++ DomainElementModel.fields

  override val `type`: List[ValueType] = List(Namespace.ApiFederation + "PrimaryKey") ++ DomainElementModel.`type`

}
