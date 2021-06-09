package amf.core.internal.plugins.document.graph
import amf.core.internal.metamodel.domain.ShapeModel
import amf.core.internal.metamodel.{Field, Obj}

object MetaModelHelper {
  def fieldsFrom(obj: Obj): Seq[Field] = {
    // workaround for lazy values in shape
    val lazyShapeFields = obj match {
      case _: ShapeModel => Seq(ShapeModel.CustomShapePropertyDefinitions, ShapeModel.CustomShapeProperties)
      case _             => Nil
    }
    obj.fields ++ lazyShapeFields
  }
}
