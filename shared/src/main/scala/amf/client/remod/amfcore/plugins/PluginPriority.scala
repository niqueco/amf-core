package amf.client.remod.amfcore.plugins

private[remod] sealed case class PluginPriority(priority: Int) {}

object HighPriority extends PluginPriority(1)

object NormalPriority extends PluginPriority(2)

object LowPriority extends PluginPriority(3)
