package amf.core.internal.metamodel.domain.common

import amf.core.client.scala.vocabulary.Namespace.Core
import amf.core.internal.metamodel.Type.Str
import amf.core.internal.metamodel.domain.{ModelDoc, ModelVocabularies}
import amf.core.internal.metamodel.{Field, Obj}

/** Description field.
  */
trait DescriptionField extends Obj {
  val Description: Field = Field(
    Str,
    Core + "description",
    ModelDoc(ModelVocabularies.Core, "description", "Human readable description of an element")
  )
}

// We should avoid doing this
// DescriptionField is not a concrete model but a trait from other models. Should be a trait not an object
object DescriptionField {
  val Description: Field = Field(
    Str,
    Core + "description",
    ModelDoc(ModelVocabularies.Core, "description", "Human readable description of an element")
  )
}
