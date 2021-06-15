package amf.core.internal.unsafe

import amf.core.internal.remote.{JsPlatform, Platform}
import amf.core.internal.remote.browser.JsBrowserPlatform
import amf.core.internal.remote.server.JsServerPlatform

import scala.scalajs.js.{Dynamic, isUndefined}

object PlatformBuilder {

  val platform: JsPlatform = if (isBrowser) new JsBrowserPlatform() else new JsServerPlatform()

  def apply(): JsPlatform = platform

  /** Return true if js is running on browser. */
  private def isBrowser: Boolean = !isUndefined(Dynamic.global.document)
}
