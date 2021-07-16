package amf.core.client.platform.plugin

import amf.core.client.common.PluginPriority

trait AMFPlugin[T] {
  val id: String
  def applies(element: T): Boolean
  def priority: PluginPriority
}
