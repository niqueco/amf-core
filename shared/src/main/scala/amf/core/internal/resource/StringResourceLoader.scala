package amf.core.internal.resource

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.remote.Mimes

import scala.concurrent.Future

case class StringResourceLoader(url: String, content: String, mediaType: Option[String]) extends ResourceLoader {

  private val _content = new Content(content, url, mediaType)

  /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
  override def fetch(resource: String): Future[Content] = Future.successful(_content)

  /** Accepts specified resource. */
  override def accepts(resource: String): Boolean = resource == url
}
