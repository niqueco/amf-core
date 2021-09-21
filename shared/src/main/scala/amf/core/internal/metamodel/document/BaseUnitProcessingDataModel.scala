package amf.core.internal.metamodel.document

import amf.core.client.scala.model.document.BaseUnitProcessingData
import amf.core.client.scala.vocabulary.Namespace.Document
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.metamodel.Type.Bool
import amf.core.internal.metamodel.domain.{ModelDoc, ModelVocabularies}
import amf.core.internal.metamodel.{Field, ModelDefaultBuilder}

trait BaseUnitProcessingDataModel extends ModelDefaultBuilder {
  val Transformed: Field = Field(
      Bool,
      Document + "transformed",
      ModelDoc(ModelVocabularies.AmlDoc,
               "transformed",
               "Indicates whether a BaseUnit was transformed with some pipeline")
  )
}

object BaseUnitProcessingDataModel extends BaseUnitProcessingDataModel {
  override val `type`: List[ValueType] = List(Document + "BaseUnitProcessingData")

  override def modelInstance: BaseUnitProcessingData = BaseUnitProcessingData()

  override def fields: List[Field] = List(Transformed)

  override val doc: ModelDoc = ModelDoc(ModelVocabularies.AmlDoc,
                                        "BaseUnitProcessingData",
                                        "Class that groups data related to how a Base Unit was processed")

}
