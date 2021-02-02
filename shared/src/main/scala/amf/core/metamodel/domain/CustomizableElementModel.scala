package amf.core.metamodel.domain

import amf.core.metamodel.Field
import amf.core.metamodel.Type.Array
import amf.core.metamodel.domain.extensions.DomainExtensionModel
import amf.core.vocabulary.Namespace.Document

trait CustomizableElementModel {
  // EDIT: Copied from DomainElementModel
  // This creates a cycle in the among DomainModels, triggering a classnotdef problem
  // I need lazy evaluation here.
  // It cannot even be defined in the list of fields below
  lazy val CustomDomainProperties = Field(
      Array(DomainExtensionModel),
      Document + "customDomainProperties",
      ModelDoc(ModelVocabularies.AmlDoc,
               "customDomainProperties",
               "Extensions provided for a particular domain element.")
  )
}

object CustomizableElementModel extends CustomizableElementModel
