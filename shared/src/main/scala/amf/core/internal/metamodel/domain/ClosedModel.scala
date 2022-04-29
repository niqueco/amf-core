package amf.core.internal.metamodel.domain

import amf.core.client.scala.vocabulary.Namespace.Shacl
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.Bool

trait ClosedModel {

  val Closed: Field = Field(
      Bool,
      Shacl + "closed",
      ModelDoc(ExternalModelVocabularies.Shacl, "closed", "Additional properties in the input node accepted constraint")
  )
}
