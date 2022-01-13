package amf.core.internal.annotations

import amf.core.client.scala.model.domain.{AmfElement, Annotation, AnnotationGraphLoader, SerializableAnnotation}

case class InlineElement() extends SerializableAnnotation {
  override val name: String = "inline-element"

  override val value: String = ""
}

object InlineElement extends AnnotationGraphLoader {
  override def unparse(value: String, objects: Map[String, AmfElement]): Option[Annotation] = {
    Some(InlineElement())
  }
}
