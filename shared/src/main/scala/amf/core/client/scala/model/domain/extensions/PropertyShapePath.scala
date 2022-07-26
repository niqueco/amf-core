package amf.core.client.scala.model.domain.extensions

import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.metamodel.domain.extensions.PropertyShapePathModel
import amf.core.internal.metamodel.domain.extensions.PropertyShapePathModel._
import amf.core.internal.parser.domain.{Annotations, Fields}
import org.yaml.model.YPart

/** Scalar shape
  */
case class PropertyShapePath private[amf] (override val fields: Fields, override val annotations: Annotations)
    extends DomainElement {

  def path: Seq[PropertyShape]                      = fields.field(Path)
  def withPath(path: Seq[PropertyShape]): this.type = setArray(Path, path)

  override val meta: PropertyShapePathModel.type = PropertyShapePathModel

  private[amf] override def componentId: String = "/property-shape-path"

}

object PropertyShapePath {
  def apply(): PropertyShapePath                         = apply(Annotations())
  def apply(ast: YPart): PropertyShapePath               = apply(Annotations(ast))
  def apply(annotations: Annotations): PropertyShapePath = PropertyShapePath(Fields(), annotations)
}
