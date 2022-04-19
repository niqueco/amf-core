package amf.core.internal.metamodel.domain.federation

import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.vocabulary.Namespace
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.Str
import amf.core.internal.metamodel.domain.{DomainElementModel, ModelDoc, ModelVocabularies}

object KeyMappingModel extends DomainElementModel {

  val PrimaryKey = Field(
      FieldReferenceModel,
      Namespace.ApiFederation + "primaryKey",
      ModelDoc(ModelVocabularies.ApiFederation, "primaryKey", "Primary key of the related declared entity")
  )

  val FieldReference = Field(
      FieldReferenceModel,
      Namespace.ApiFederation + "fieldReference",
      ModelDoc(ModelVocabularies.ApiFederation, "fieldReference", "Provided field of the local entity")
  )

  override def modelInstance: AmfObject = ???

  override def fields: List[Field] = ???
}
