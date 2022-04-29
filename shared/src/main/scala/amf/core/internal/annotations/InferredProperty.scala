package amf.core.internal.annotations

import amf.core.client.scala.model.domain.{AmfElement, AnnotationGraphLoader, EternalSerializedAnnotation}

// This should be in amf-shapes BUT PropertyShape is in amf-core
/** JSON-Schema based specs: Signals a property that was generated because it is part of the required array and is not
  * explicitly defined
  */
case class InferredProperty() extends EternalSerializedAnnotation {
  override val name: String  = "inferred-property"
  override val value: String = ""
}

object InferredProperty extends AnnotationGraphLoader {
  override def unparse(value: String, objects: Map[String, AmfElement]): Option[InferredProperty] = {
    Some(InferredProperty())
  }
}
