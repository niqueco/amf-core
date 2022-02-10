package amf.core.internal.metamodel.domain.federation

import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.vocabulary.Namespace
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.{Array, Str}
import amf.core.internal.metamodel.domain.{DomainElementModel, ModelDoc, ModelVocabularies}

object EntityProviderModel extends DomainElementModel {

  val Fields = Field(
      Array(Str),
      Namespace.ApiFederation + "entityReferenceFieldName",
      ModelDoc(ModelVocabularies.ApiFederation,
               "entityReferenceFieldName",
               "List of field names that compose this key")
  )

  val KeyMapping = Field(
      Array(KeyMappingModel),
      Namespace.ApiFederation + "keyMapping",
      ModelDoc(ModelVocabularies.ApiFederation, "keyMapping", "List of field names that compose this key")
  )

  override def modelInstance: AmfObject = ???

  override def fields: List[Field] = List(Fields) ++ DomainElementModel.fields
}
