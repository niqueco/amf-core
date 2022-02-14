package amf.core.client.scala.traversal

import amf.core.client.scala.model.domain.Shape
import amf.core.internal.annotations.TypeAlias

case class ShapeTraversalRegistry() extends ModelTraversalRegistry() {
  // IDs of elements that do not throw recursion errors
  private[amf] var allowList: Set[String] = Set()

  def isAllowListed(id: String): Boolean = allowList.contains(id)

  private var allowedCycleClasses: Seq[Class[_]] = Seq()

  def withAllowedCyclesInstances(classes: Seq[Class[_]]): this.type = {
    allowedCycleClasses = classes
    this
  }

  def isAllowedToCycle(shape: Shape): Boolean = allowedCycleClasses.contains(shape.getClass)

  def allow(shapeIds: Set[String])(fnc: () => Shape): Shape = {
    val previousAllowList = allowList
    allowList = allowList ++ shapeIds
    val expanded = runNested(_ => fnc())
    allowList = previousAllowList
    expanded
  }

  def shouldFailIfRecursive(root: Shape, shape: Shape): Boolean = root.annotations.find(classOf[TypeAlias]) match {
    case Some(alias) =>
      val a = alias.aliasId.equals(shape.id) && currentPath.nonEmpty || isInCurrentPath(shape.id)
      val b = !isAllowedToCycle(shape)
      a && b
    case None => isInCurrentPath(shape.id) && !isAllowedToCycle(shape)
  }

}
