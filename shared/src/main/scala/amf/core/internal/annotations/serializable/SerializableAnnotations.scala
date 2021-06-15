package amf.core.internal.annotations.serializable

import amf.core.client.scala.model.domain.AnnotationGraphLoader

private[amf] abstract class SerializableAnnotations {

  val annotations: Map[String, AnnotationGraphLoader]

}
