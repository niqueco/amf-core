package amf.core.internal.metamodel.document

import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.Iri
import amf.core.internal.metamodel.domain.{ModelDoc, ModelVocabularies}
import amf.core.client.scala.vocabulary.Namespace.Document
import amf.core.client.scala.vocabulary.ValueType

/** A Document that extends a target document, overwriting part of the information or overlaying additional information.
  */
trait ExtensionLikeModel extends DocumentModel {

  /** Document that is going to be extended overlaying or adding additional information
    */
  val Extends: Field = Field(
    Iri,
    Document + "extends",
    ModelDoc(ModelVocabularies.AmlDoc, "extends", "Target base unit being extended by this extension model")
  )

  override val fields: List[Field] = Extends :: DocumentModel.fields

}

object ExtensionLikeModel extends ExtensionLikeModel {

  override val `type`: List[ValueType] = List(Document + "DocumentExtension") ++ BaseUnitModel.`type`

  override val doc: ModelDoc = ModelDoc(
    ModelVocabularies.AmlDoc,
    "DocumentExtension",
    "A Document that extends a target document, overwriting part of the information or overlaying additional information."
  )
}
