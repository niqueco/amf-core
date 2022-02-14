package amf.core.client.scala.traversal

import amf.core.client.scala.model.domain.Shape
import amf.core.internal.annotations.TypeAlias

import scala.collection.mutable

case class ShapeTraversalRegistry() extends ModelTraversalRegistry() {
  // IDs of elements that do not throw recursion errors
  protected var allowList: Set[String] = Set()

  def isAllowListed(id: String): Boolean = allowList.contains(id)

  private var allowedCycleClasses: Seq[Class[_]] = Seq()

  def withAllowedCyclesInstances(classes: Seq[Class[_]]): this.type = {
    allowedCycleClasses = classes
    this
  }

  def isAllowedToCycle(shape: Shape): Boolean = allowedCycleClasses.contains(shape.getClass)

  def runWithIgnoredId(fnc: () => Shape, shapeId: String): Shape = runWithIgnoredIds(fnc, Set(shapeId))

  def runWithIgnoredIds(fnc: () => Shape, shapeIds: Set[String]): Shape = {
    val previousAllowList = allowList
    allowList = allowList ++ shapeIds
    val expanded = runNested(_ => fnc())
    allowList = previousAllowList
    expanded
  }

  def recursionAllowed(fnc: () => Shape, shapeId: String): Shape = {
    val actual = currentPath + shapeId
    runWithIgnoredIds(fnc, actual)
  }

  def shouldFailIfRecursive(root: Shape, shape: Shape): Boolean = root.annotations.find(classOf[TypeAlias]) match {
    case Some(alias) =>
      val a = alias.aliasId.equals(shape.id) && currentPath.nonEmpty || isInCurrentPath(shape.id)
      val b = !isAllowedToCycle(shape)
      a && b
    case None => isInCurrentPath(shape.id) && !isAllowedToCycle(shape)
  }

}
