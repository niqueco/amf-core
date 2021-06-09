package amf.core.internal.metamodel.document

import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.Array
import amf.core.internal.metamodel.domain.extensions.DomainExtensionModel
import amf.core.internal.metamodel.domain.{DomainElementModel, ModelDoc, ModelVocabularies}
import amf.core.client.scala.model.document.Module
import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.vocabulary.Namespace.Document
import amf.core.client.scala.vocabulary.ValueType

/**
  * Module metamodel
  *
  * A Module is a parsing Unit that declares DomainElements that can be referenced from the DomainElements in other parsing Units.
  * It main purpose is to expose the declared references so they can be re-used
  */
trait ModuleModel extends BaseUnitModel {

  /**
    * The declares relationship exposes a DomainElement as a re-usable unit that can be referenced from other units.
    * URIs for the declared DomainElement are considered to be stable and safe to reference from other DomainElements.
    */
  val Declares: Field = Field(
      Array(DomainElementModel),
      Document + "declares",
      ModelDoc(
          ModelVocabularies.AmlDoc,
          "declares",
          "The declares relationship exposes a DomainElement as a re-usable unit that can be referenced from other units.\nURIs for the declared DomainElement are considered to be stable and safe to reference from other DomainElements."
      )
  )

  lazy val CustomDomainProperties: Field = Field(
      Array(DomainExtensionModel),
      Document + "customDomainProperties",
      ModelDoc(ModelVocabularies.AmlDoc,
               "customDomainProperties",
               "Extensions provided for a particular domain element.")
  )

  override def modelInstance: AmfObject = Module()
}

object ModuleModel extends ModuleModel {

  override val `type`: List[ValueType] = List(Document + "Module") ++ BaseUnitModel.`type`

  override val fields: List[Field] = Declares :: BaseUnitModel.fields

  override val doc: ModelDoc = ModelDoc(
      ModelVocabularies.AmlDoc,
      "Module",
      "A Module is a parsing Unit that declares DomainElements that can be referenced from the DomainElements in other parsing Units.\nIt main purpose is to expose the declared references so they can be re-used"
  )
}
