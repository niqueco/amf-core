package amf.core.internal.metamodel.domain.extensions

import amf.core.client.scala.model.domain.extensions.PropertyShape
import amf.core.client.scala.vocabulary.Namespace.{Federation, Shacl, Shapes}
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.{Array, Int, Iri, Str}
import amf.core.internal.metamodel.domain._

/** Property shape metamodel
  *
  * Model for SHACL PropertyShapes
  */
object PropertyShapeModel extends ShapeModel {

  val Path: Field =
    Field(Iri, Shacl + "path", ModelDoc(ExternalModelVocabularies.Shacl, "path", "Path to the constrained property"))

  val Range: Field =
    Field(ShapeModel, Shapes + "range", ModelDoc(ModelVocabularies.Shapes, "range", "Range property constraint"))

  val SerializationOrder: Field =
    Field(
      Int,
      Shapes + "serializationOrder",
      ModelDoc(
        ModelVocabularies.Shapes,
        "serializationOrder",
        "position in the set of properties for a shape used to serialize this property on the wire"
      )
    )

  val MinCount: Field = Field(
    Int,
    Shacl + "minCount",
    ModelDoc(ExternalModelVocabularies.Shacl, "minCount", "Minimum count property constraint")
  )

  val MaxCount: Field = Field(
    Int,
    Shacl + "maxCount",
    ModelDoc(ExternalModelVocabularies.Shacl, "maxCount", "Maximum count property constraint")
  )

  val PatternName: Field = Field(
    Str,
    Shapes + "patternName",
    ModelDoc(ModelVocabularies.Shapes, "patternName", "Patterned property constraint")
  )

  val Requires: Field = Field(
    Array(PropertyShapePathModel),
    Federation + "requires",
    ModelDoc(ModelVocabularies.Federation, "requires", "External properties (by path) required to retrieve data from this property during federation")
  )

  val Provides: Field = Field(
    Array(PropertyShapePathModel),
    Federation + "provides",
    ModelDoc(ModelVocabularies.Federation, "provides", "External properties (by path) that can be provided by this graph during federation")
  )

  override val `type`: List[ValueType] = List(Shacl + "PropertyShape") ++ ShapeModel.`type`

  override def fields: List[Field] =
    List(
      Path,
      Range,
      MinCount,
      MaxCount,
      PatternName,
      SerializationOrder,
      Requires,
      Provides,
    ) ++ ShapeModel.fields ++ DomainElementModel.fields

  override def modelInstance: PropertyShape = PropertyShape()

  override val doc: ModelDoc = ModelDoc(
    ModelVocabularies.Shapes,
    "PropertyShape",
    "Constraint over a property in a data shape."
  )
}
