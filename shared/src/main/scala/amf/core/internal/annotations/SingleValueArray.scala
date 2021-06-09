package amf.core.internal.annotations

import amf.core.client.scala.model.domain.{AmfElement, Annotation, AnnotationGraphLoader, SerializableAnnotation}

case class SingleValueArray() extends SerializableAnnotation {

  /** Extension name. */
  override val name: String = "single-value-array"

  /** Value as string. */
  override val value: String = ""

}
object SingleValueArray extends AnnotationGraphLoader {
  override def unparse(value: String, objects: Map[String, AmfElement]): Option[Annotation] =
    Some(SingleValueArray())
}
