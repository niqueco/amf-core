package amf.core.traversal.iterator

import amf.core.model.domain.{AmfElement, AmfObject}

import scala.collection.mutable

trait VisitedCollector {
  def visited(element: AmfElement): Boolean
  def +=(element: AmfElement): Unit
}

case class IdCollector(visited: mutable.Set[String] = mutable.Set.empty) extends VisitedCollector {
  override def visited(element: AmfElement): Boolean = element match {
    case obj: AmfObject => visited.contains(obj.id)
    case _ => false
  }

  override def +=(element: AmfElement): Unit = element match {
    case obj: AmfObject => visited += obj.id
    case _ =>
  }
}

case class InstanceCollector(visited: mutable.Set[AmfElement] = mutable.Set.empty) extends VisitedCollector {
  override def visited(element: AmfElement): Boolean = visited.contains(element)
  override def +=(element: AmfElement): Unit = visited += element
}
