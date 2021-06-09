package amf.core.internal.annotations

import amf.core.client.scala.model.domain.SerializableAnnotation

case class ExtendsDialectNode(extendedNode: String) extends SerializableAnnotation {

  /** Extension name. */
  override val name: String = "extendsNode"

  /** Value as string. */
  override val value: String = extendedNode

}
