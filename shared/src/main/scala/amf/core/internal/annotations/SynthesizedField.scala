package amf.core.internal.annotations

import amf.core.client.scala.model.domain._

trait VirtualNode extends Annotation

case class SynthesizedField() extends SerializableAnnotation with VirtualNode {
  override val name: String  = "synthesized-field"
  override val value: String = "true"
}

object SynthesizedField extends AnnotationGraphLoader {
  override def unparse(value: String, objects: Map[String, AmfElement]): Option[Annotation] =
    Some(SynthesizedField())
}

case class Inferred() extends VirtualNode

case class VirtualElement() extends SerializableAnnotation with PerpetualAnnotation with VirtualNode {
  override val name: String  = "virtual-element"
  override def value: String = "true"
}

object VirtualElement extends AnnotationGraphLoader {
  override def unparse(annotatedValue: String, objects: Map[String, AmfElement]): Option[Annotation] = Some(
    VirtualElement()
  )

  def name: String = "virtual-element"
}
