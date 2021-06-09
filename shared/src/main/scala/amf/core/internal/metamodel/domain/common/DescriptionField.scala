package amf.core.internal.metamodel.domain.common

import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.Str
import amf.core.internal.metamodel.domain.{ModelDoc, ModelVocabularies}
import amf.core.client.scala.vocabulary.Namespace.Core

/**
  * Description field.
  */
trait DescriptionField {
  val Description = Field(Str,
                          Core + "description",
                          ModelDoc(ModelVocabularies.Core, "description", "Human readable description of an element"))
}

object DescriptionField extends DescriptionField
