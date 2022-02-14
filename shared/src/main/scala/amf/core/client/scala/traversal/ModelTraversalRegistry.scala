package amf.core.client.scala.traversal

import scala.collection.mutable

class ModelTraversalRegistry() {

  // All IDs visited in the traversal
  protected var visitedIds: mutable.Set[String] = mutable.Set()

  // IDs visited in the current path
  protected var currentPath: Set[String] = Set.empty

  def +(id: String): this.type = {
    visitedIds += id
    currentPath += id
    this
  }

  def isInCurrentPath(id: String): Boolean = currentPath.contains(id)

  def wasVisited(id: String): Boolean = visitedIds.contains(id)

  // Runs a function and restores the currentPath to its original state after the run
  def runNested[T](fnc: this.type => T): T = {
    val previousPath = currentPath
    val element      = fnc(this)
    currentPath = previousPath
    element
  }

}
