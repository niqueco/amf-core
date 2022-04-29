package amf.core.client.scala.traversal.iterator
import amf.core.client.scala.model.domain.{AmfArray, AmfElement, AmfObject}

import scala.collection.mutable

case class AmfElementIterator private (var buffer: List[AmfElement], visited: VisitedCollector) extends AmfIterator {

  override def hasNext: Boolean = buffer.nonEmpty

  override def next: AmfElement = {
    val current = buffer.head
    buffer = buffer.tail
    advance()
    current
  }

  private def advance(): Unit = {
    if (buffer.nonEmpty) {
      val current = buffer.head
      buffer = buffer.tail
      if (visited.visited(current)) {
        advance()
      } else {
        current match {
          case obj: AmfObject =>
            val elements = obj.fields.fields().map(_.element)
            visited += obj
            buffer = current :: elements.toList ++ buffer
          case arr: AmfArray =>
            buffer = current :: arr.values.toList ++ buffer
          case _ =>
            buffer = current :: buffer
        }
      }
    }
  }

}

object AmfElementIterator {
  def apply(elements: List[AmfElement], visited: VisitedCollector = IdCollector()): AmfElementIterator = {
    val iterator = new AmfElementIterator(elements, visited)
    iterator.advance()
    iterator
  }
}
