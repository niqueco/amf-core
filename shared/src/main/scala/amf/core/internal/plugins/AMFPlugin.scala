package amf.core.internal.plugins

import amf.core.client.common.PluginPriority

private[amf] trait AMFPlugin[T] {
  val id: String
  def applies(element: T): Boolean
  // test for collisions?
  def priority: PluginPriority //?
}

object AMFPlugin {

  implicit def ordering[A <: AMFPlugin[_]]: Ordering[A] = (x: A, y: A) => {
    x.priority.priority compareTo (y.priority.priority)
  }

}
