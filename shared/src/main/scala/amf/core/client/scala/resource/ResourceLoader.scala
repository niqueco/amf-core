package amf.core.client.scala.resource

import amf.core.client.common.remote.Content

import scala.concurrent.Future

trait ResourceLoader {

  /** Fetch specified resource and return associated content. Resource should have been previously accepted. If the
    * resource doesn't exists, it returns a failed future caused by a ResourceNotFound exception.
    */
  def fetch(resource: String): Future[Content]

  /** Checks if the resource loader accepts the specified resource. */
  def accepts(resource: String): Boolean
}
