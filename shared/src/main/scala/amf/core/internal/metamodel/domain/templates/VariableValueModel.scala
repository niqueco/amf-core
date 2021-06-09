package amf.core.internal.metamodel.domain.templates

import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.Str
import amf.core.internal.metamodel.domain.{DataNodeModel, DomainElementModel, ModelDoc, ModelVocabularies}
import amf.core.client.scala.model.domain.templates.VariableValue
import amf.core.client.scala.vocabulary.Namespace.{Document, Core}
import amf.core.client.scala.vocabulary.ValueType

object VariableValueModel extends DomainElementModel {

  val Name = Field(Str, Core + "name", ModelDoc(ModelVocabularies.Core, "name", "name of the template variable"))

  val Value =
    Field(DataNodeModel, Document + "value", ModelDoc(ModelVocabularies.AmlDoc, "value", "value of the variables"))

  override def fields: List[Field] = List(Name, Value) ++ DomainElementModel.fields

  override val `type`: List[ValueType] = Document + "VariableValue" :: DomainElementModel.`type`

  override def modelInstance = VariableValue()

  override val doc: ModelDoc = ModelDoc(
      ModelVocabularies.AmlDoc,
      "VariableValue",
      "Value for a variable in a graph template"
  )
}
