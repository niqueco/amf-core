package amf.core.internal.plugins.document.graph.emitter.utils

import amf.core.client.scala.model.domain.{AmfElement, AmfScalar}
import org.yaml.builder.DocBuilder.{Part, SType}

trait ScalarEmitter[T] {
  protected def scalar(b: Part[T], content: String, t: SType): Unit
  protected def scalar(b: Part[T], content: String): Unit = scalar(b, content, SType.Str)
  protected def scalar(b: Part[T], content: AmfElement, t: SType): Unit = scalar(b, stringFrom(content), t)

  protected def emitScalar(b: Part[T], content: String, t: SType): Unit
  protected def emitScalar(b: Part[T], content: String): Unit = emitScalar(b, content, SType.Str)

  protected def emitScalar(b: Part[T], content: AmfElement, t: SType): Unit
  protected def emitScalar(b: Part[T], content: AmfElement): Unit = emitScalar(b, content, SType.Str)

  private def stringFrom(element: AmfElement) = element.asInstanceOf[AmfScalar].value.toString

}
