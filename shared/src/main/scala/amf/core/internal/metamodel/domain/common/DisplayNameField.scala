package amf.core.internal.metamodel.domain.common

import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.Str
import amf.core.internal.metamodel.domain.{ModelVocabularies, ModelDoc}
import amf.core.client.scala.vocabulary.Namespace.Core

/**
  * DisplayName field.
  */
trait DisplayNameField {
  val DisplayName = Field(Str,
                          Core + "displayName",
                          ModelDoc(ModelVocabularies.Core, "displayName", "Human readable name for the term"))
}

object DisplayNameField extends DisplayNameField
