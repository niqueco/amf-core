package amf.core.client.scala.traversal.iterator

import amf.core.client.scala.model.domain.AmfElement

trait IteratorStrategy {
  def iterator(elements: List[AmfElement], visited: VisitedCollector): AmfIterator
}

object AmfElementStrategy extends IteratorStrategy {
  override def iterator(elements: List[AmfElement], visited: VisitedCollector = IdCollector()): AmfIterator =
    AmfElementIterator(elements, visited)
}

object DomainElementStrategy extends IteratorStrategy {
  override def iterator(elements: List[AmfElement], visited: VisitedCollector = IdCollector()): AmfIterator =
    DomainElementIterator(elements, visited)
}
