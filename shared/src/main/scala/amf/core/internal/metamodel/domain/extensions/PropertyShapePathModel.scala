package amf.core.internal.metamodel.domain.extensions

import amf.core.client.scala.model.domain.extensions.PropertyShapePath
import amf.core.client.scala.vocabulary.Namespace.Shapes
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.SortedArray
import amf.core.internal.metamodel.domain.{DomainElementModel, ModelDoc, ModelVocabularies}

object PropertyShapePathModel extends DomainElementModel {

  val Path: Field =
    Field(
      SortedArray(PropertyShapeModel),
      Shapes + "path",
      ModelDoc(
        ModelVocabularies.Shapes,
        "path",
        "represents a property shape path in a traversal to reach a particular Shape"
      )
    )

  override def modelInstance: PropertyShapePath = PropertyShapePath()

  override val `type`: List[ValueType] = Shapes + "PropertyShapePath" :: DomainElementModel.`type`

  override val fields: List[Field] = Path :: DomainElementModel.fields

  override val doc: ModelDoc = ModelDoc(
    ModelVocabularies.Shapes,
    "PropertyShapePath",
    "Model that represents a property shape path in a traversal to reach a particular Shape"
  )
}
