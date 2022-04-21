package amf.core.internal.metamodel.domain.federation

import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.vocabulary.Namespace.{ApiFederation, Document}
import amf.core.client.scala.vocabulary.{Namespace, ValueType}
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.{Array, Str}
import amf.core.internal.metamodel.domain.{DomainElementModel, ModelDoc, ModelVocabularies}

object EntityReferenceModel extends DomainElementModel {

  val ReferenceFields = Field(
      Array(FieldReferenceModel),
      Namespace.ApiFederation + "entityReferenceField",
      ModelDoc(ModelVocabularies.ApiFederation, "entityReferenceField", "List of field names that compose this key")
  )

  val KeyMapping = Field(
      Array(KeyMappingModel),
      Namespace.ApiFederation + "keyMapping",
      ModelDoc(ModelVocabularies.ApiFederation, "keyMapping", "List of field names that compose this key")
  )

  override def modelInstance: AmfObject = ???

  override val fields: List[Field] = List(ReferenceFields) ++ DomainElementModel.fields

  override val `type`: List[ValueType] = List(ApiFederation + "EntityReference", Document + "DomainElement")

}

object FieldReferenceModel extends DomainElementModel {

  val Expression = Field(
      Str,
      Namespace.Core + "referenceExpression",
      ModelDoc(ModelVocabularies.Core, "referenceExpression", "Original expression before process target")
  )

  val Target = Field(
      DomainElementModel,
      Namespace.Core + "referenceTarget",
      ModelDoc(ModelVocabularies.Core, "referenceTarget", "Final target of the expression")
  )

  val Language = Field(
      Str,
      Namespace.Core + "language",
      ModelDoc(ModelVocabularies.Core, "language", "Original laguage of the expression")
  )

  override def modelInstance: AmfObject = ???

  override def fields: List[Field] = List(Expression, Target, Language) ++ DomainElementModel.fields

  override val `type`: List[ValueType] = List(Namespace.Core + "FieldReference", Document + "DomainElement")
}
