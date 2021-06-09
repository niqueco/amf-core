package amf.core.internal.metamodel.domain.templates

import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.{Array, Str}
import amf.core.internal.metamodel.domain.common.{DescriptionField, NameFieldSchema}
import amf.core.internal.metamodel.domain._
import amf.core.client.scala.vocabulary.Namespace._
import amf.core.client.scala.vocabulary.ValueType

trait AbstractDeclarationModel extends DomainElementModel with KeyField with NameFieldSchema with DescriptionField {

  val DataNode = Field(
      DataNodeModel,
      Document + "dataNode",
      ModelDoc(ModelVocabularies.AmlDoc, "dataNode", "Associated dynamic structure for the declaration"))

  val Variables = Field(
      Array(Str),
      Document + "variable",
      ModelDoc(ModelVocabularies.AmlDoc,
               "variable",
               "Variables to be replaced in the graph template introduced by an AbstractDeclaration")
  )

  override val key: Field = Name

  override def fields: List[Field] =
    List(Name, Description, DataNode, Variables) ++ LinkableElementModel.fields ++ DomainElementModel.fields
}

object AbstractDeclarationModel extends AbstractDeclarationModel {
  override val `type`: List[ValueType] = Document + "AbstractDeclaration" :: DomainElementModel.`type`

  override def modelInstance = throw new Exception("AbstractDeclarationModel is abstract and cannot be instantiated")

  override val doc: ModelDoc = ModelDoc(
      ModelVocabularies.AmlDoc,
      "AbstractDeclaration",
      "Graph template that can be used to declare a re-usable graph structure that can be applied to different domain elements\nin order to re-use common semantics. Similar to a Lisp macro or a C++ template.\nIt can be extended by any domain element adding bindings for the variables in the declaration."
  )
}
