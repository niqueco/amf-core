package amf.core.traversal.iterator
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject, DomainElement}

import scala.collection.mutable

case class DomainElementIterator private (var buffer: List[AmfElement], visited: VisitedCollector) extends AmfIterator {

  override def hasNext: Boolean = buffer.nonEmpty

  override def next: AmfElement = {
    val current = buffer.head
    buffer = buffer.tail
    advance()
    current
  }

  private def advance(): Unit = {
    if(buffer.nonEmpty) {
      val current = buffer.head
      buffer = buffer.tail
      if (visited.visited(current)) {
        advance()
      }
      else {
        current match {
          case obj: AmfObject =>
            val elements = obj.fields.fields().map(_.element).toList
            visited += obj
            obj match {
              case domain: DomainElement =>
                buffer = domain :: elements ++ buffer
              // advance finishes here because a non visited domain element was found
              case _ =>
                buffer = elements ++ buffer
                advance()
            }
          case arr: AmfArray =>
            buffer = arr.values.toList ++ buffer
            advance()
          case _ =>
            advance()
        }
      }
    }
  }

}

object DomainElementIterator {

  def apply(elements: List[AmfElement], visited: VisitedCollector = IdCollector()): DomainElementIterator = {
    val iterator = new DomainElementIterator(elements, visited)
    iterator.advance()
    iterator
  }

}
