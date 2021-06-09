package amf.core.client.platform.resource

import amf.core.internal.convert.CoreClientConverters.ClientFuture
import amf.core.client.common.remote.Content

import scala.scalajs.js

@js.native
trait ClientResourceLoader extends js.Object {

  /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
  /** If the resource not exists, you should return a future failed with an ResourceNotFound exception. */
  def fetch(resource: String): ClientFuture[Content] = js.native

  /** Accepts specified resource. */
  def accepts(resource: String): Boolean = js.native
}
