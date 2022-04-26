package amf.core.internal.unsafe

import amf.core.internal.remote.JvmPlatform

object PlatformBuilder {
  val platform             = new JvmPlatform()
  def apply(): JvmPlatform = platform
}
