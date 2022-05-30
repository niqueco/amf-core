package amf.core.internal.metamodel.domain.templates

import amf.core.internal.metamodel.Type.Bool
import amf.core.internal.metamodel.domain.{ModelDoc, ModelVocabularies}
import amf.core.internal.metamodel.{Field, Obj}
import amf.core.client.scala.vocabulary.Namespace

/** Determines if the field is optional for merging.
  */
trait OptionalField extends Obj {
  val Optional = Field(
      Bool,
      Namespace.ApiContract + "optional",
      ModelDoc(ModelVocabularies.ApiContract, "optional", "Marks some information as optional")
  )
}
