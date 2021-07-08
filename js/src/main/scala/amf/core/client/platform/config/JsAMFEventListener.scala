package amf.core.client.platform.config

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("AMFEventListenerFactory")
object AMFEventListenerFactory {
  def from(listener: JsAMFEventListener): AMFEventListener = (event: AMFEvent) => listener.notifyEvent(event)
}

@js.native
trait JsAMFEventListener extends js.Object {
  def notifyEvent(event: AMFEvent): Unit = js.native
}
