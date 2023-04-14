package amf.core.internal.metamodel.document

import amf.core.client.scala.vocabulary.Namespace.Document
import amf.core.client.scala.vocabulary.{Namespace, ValueType}
import amf.core.internal.metamodel.Type.{Array, Bool, Iri, Str}
import amf.core.internal.metamodel.domain.{ModelDoc, ModelVocabularies}
import amf.core.internal.metamodel.{Field, ModelDefaultBuilder}

import scala.annotation.nowarn

/** BaseUnit metamodel
  *
  * Base class for every single document model unit. After parsing a document the parser generate parsing Base Units.
  * Base Units encode the domain elements and can reference other units to re-use descriptions.
  */
trait BaseUnitModel extends ModelDefaultBuilder {

  val Root: Field = Field(
    Bool,
    Document + "root",
    ModelDoc(
      ModelVocabularies.AmlDoc,
      "root",
      "Indicates if the base unit represents the root of the document model obtained from parsing"
    )
  )

  val Location: Field = Field(
    Str,
    Document + "location",
    ModelDoc(ModelVocabularies.AmlDoc, "location", "Location of the metadata document that generated this base unit")
  )

  val Package: Field = Field(
    Str,
    Document + "package",
    ModelDoc(
      ModelVocabularies.AmlDoc,
      "package",
      "Logical identifier providing a common namespace for the information in this base unit"
    )
  )

  val References: Field = Field(
    Array(BaseUnitModel),
    Document + "references",
    ModelDoc(ModelVocabularies.AmlDoc, "references", "references across base units")
  )

  val Usage: Field = Field(
    Str,
    Document + "usage",
    ModelDoc(
      ModelVocabularies.AmlDoc,
      "usage",
      "Human readable description of the unit",
      superClasses = Seq((Namespace.Core + "description").iri())
    )
  )

  @deprecated("Use DialectInstanceProcessingDataModel.DefinedBy instead", "AMF 5.0.0 & AML 6.0.0")
  val DescribedBy: Field = Field(
    Iri,
    ValueType(Namespace.Meta, "describedBy"),
    ModelDoc(
      ModelVocabularies.AmlDoc,
      "describedBy",
      "Link to the AML dialect describing a particular subgraph of information"
    ),
    deprecated = true
  )

  @deprecated("Use APIContractProcessingDataModel.APIContractModelVersion instead", "AMF 5.0.0 & AML 6.0.0")
  val ModelVersion: Field =
    Field(
      Str,
      Document + "version",
      ModelDoc(ModelVocabularies.AmlDoc, "version", "Version of the current model"),
      deprecated = true
    )

  val ProcessingData: Field =
    Field(
      BaseUnitProcessingDataModel,
      Document + "processingData",
      ModelDoc(
        ModelVocabularies.AmlDoc,
        "processingData",
        "Field with utility data to be used in Base Unit processing"
      )
    )

  val SourceInformation: Field =
    Field(
      BaseUnitSourceInformationModel,
      Document + "sourceInformation",
      ModelDoc(
        ModelVocabularies.AmlDoc,
        "sourceInformation",
        "Contains information of the source from which the base unit was generated"
      )
    )

}

object BaseUnitModel extends BaseUnitModel {

  override val `type`: List[ValueType] = List(Document + "Unit")

  @nowarn
  override val fields: List[Field] =
    List(ModelVersion, References, Usage, DescribedBy, Root, Package, ProcessingData, SourceInformation)

  override def modelInstance = throw new Exception("BaseUnit is an abstract class")

  override val doc: ModelDoc = ModelDoc(
    ModelVocabularies.AmlDoc,
    "BaseUnit",
    "Base class for every single document model unit. After parsing a document the parser generate parsing Units. Units encode the domain elements and can reference other units to re-use descriptions."
  )
}
