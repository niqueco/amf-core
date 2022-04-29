package amf.core.client.scala.traversal

import scala.collection.mutable

class ModelTraversalRegistry() {

  // All IDs visited in the traversal
  private[amf] var visitedIds: mutable.Set[String] = mutable.Set()

  // IDs visited in the current path
  private[amf] var currentPath: Set[String] = Set.empty

  /** * TODO (Refactor needed): Traversal stack management Responsibility for managing the traversal stack is split
    * across the ModelTraversalRegistry is its callers. Callers push elements onto the stack by calling the `+` method.
    * The ModelTraversalRegistry pops elements from the stack with `runNested`, which also executes a function self.type
    * \=> T that eventually pushes more elements onto the stack. Stack management is a complex tandem between the
    * ModelTraversalRegistry and its callers which should be simplified to better debug traversals.
    */
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
