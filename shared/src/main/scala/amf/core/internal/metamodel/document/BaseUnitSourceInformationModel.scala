package amf.core.internal.metamodel.document

import amf.core.client.scala.model.document.{BaseUnitSourceInformation, LocationInformation}
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.metamodel.{Field, ModelDefaultBuilder}
import amf.core.client.scala.vocabulary.Namespace.Document
import amf.core.internal.metamodel.Type.{Array, Bool, Iri, Str}
import amf.core.internal.metamodel.domain.{ModelDoc, ModelVocabularies}

object BaseUnitSourceInformationModel extends ModelDefaultBuilder {

  val RootLocation: Field = Field(
      Str,
      Document + "rootLocation",
      ModelDoc(ModelVocabularies.AmlDoc, "rootLocation", "Root source location of base unit")
  )

  val AdditionalLocations: Field =
    Field(
        Array(LocationInformationModel),
        Document + "additionalLocations",
        ModelDoc(
            ModelVocabularies.AmlDoc,
            "additionalLocations",
            "Additional source locations from which certain elements where parsed"
        )
    )

  override val `type`: List[ValueType] = List(Document + "BaseUnitSourceInformation")

  override def modelInstance: BaseUnitSourceInformation = BaseUnitSourceInformation()

  override def fields: List[Field] = List(RootLocation, AdditionalLocations)

  override val doc: ModelDoc = ModelDoc(
      ModelVocabularies.AmlDoc,
      "BaseUnitSourceInformation",
      "Class that stores information of the source from which the base unit was parsed"
  )
}

object LocationInformationModel extends ModelDefaultBuilder {

  val Elements: Field =
    Field(
        Array(Iri),
        Document + "elements",
        ModelDoc(ModelVocabularies.AmlDoc, "elements", "Elements which all belong to a certain source location")
    )

  override val `type`: List[ValueType] = List(Document + "LocationInformation")

  override def modelInstance: LocationInformation = LocationInformation()

  override def fields: List[Field] = List(BaseUnitModel.Location, Elements) // reusing location field from BaseUnit.

  override val doc: ModelDoc = ModelDoc(
      ModelVocabularies.AmlDoc,
      "LocationInformation",
      "Class that store a specific location and the elements that where parsed from this source"
  )
}
