package amf.core.internal.metamodel.domain

import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.vocabulary.Namespace.Core
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.domain.common.NameFieldSchema

trait CoreTagModel extends DomainElementModel with NameFieldSchema {

  override val `type`: List[ValueType] = Core + "Tag" :: DomainElementModel.`type`

  override val doc: ModelDoc = ModelDoc(
      ModelVocabularies.ApiContract,
      "Tag",
      "Categorical information provided by some API spec format. Tags are extensions to the model supported directly in the input API spec format."
  )

  override def fields: List[Field] =
    List(
        Name
    ) ++ DomainElementModel.fields
}

object CoreTagModel extends CoreTagModel {
  override def modelInstance: AmfObject = CoreTag()
}
