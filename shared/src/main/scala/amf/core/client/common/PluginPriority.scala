package amf.core.client.common

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("PluginPriority")
sealed case class PluginPriority(priority: Int) {}

@JSExportTopLevel("HighPriority")
object HighPriority extends PluginPriority(1)

@JSExportTopLevel("NormalPriority")
object NormalPriority extends PluginPriority(2)

@JSExportTopLevel("LowPriority")
object LowPriority extends PluginPriority(3)
