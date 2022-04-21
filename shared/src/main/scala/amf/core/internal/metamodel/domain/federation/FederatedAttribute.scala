package amf.core.internal.metamodel.domain.federation

import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.vocabulary.Namespace.{ApiFederation, Document}
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.{Array, Bool}
import amf.core.internal.metamodel.domain.DomainElementModel.Extends
import amf.core.internal.metamodel.domain.{DomainElementModel, ModelDoc, ModelVocabularies}

trait FederatedAttribute extends DomainElementModel {

  val External =
    Field(Bool, ApiFederation + "external", ModelDoc(ModelVocabularies.ApiFederation, "external", "External property"))

  val Provides = Field(EntityReferenceModel,
                       ApiFederation + "provides",
                       ModelDoc(ModelVocabularies.ApiFederation, "provides", "External property"))

  val Requires = Field(EntityReferenceModel,
                       ApiFederation + "requires",
                       ModelDoc(ModelVocabularies.ApiFederation, "requires", "required field"))

  override val fields: List[Field] = List(External, Provides, Requires) ++ DomainElementModel.fields

  override val `type`: List[ValueType] = List(ApiFederation + "FederatedAttribute", Document + "DomainElement")

}

object FederatedAttribute extends FederatedAttribute {
  override def modelInstance: AmfObject = throw new Exception("FederatedAttribute is an abstract class")

}
