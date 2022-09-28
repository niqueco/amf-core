package amf.core.internal.annotations
import amf.core.client.scala.model.domain._

/*
  This annotation is used in OAS 3.0 to differentiate parameter declarations from header declarations
 */
case class DeclaredHeader() extends EternalSerializedAnnotation {
  override val name: String  = "declared-header"
  override val value: String = ""
}

object DeclaredHeader extends AnnotationGraphLoader {
  override def unparse(value: String, objects: Map[String, AmfElement]): Option[Annotation] =
    Some(DeclaredHeader())
}
