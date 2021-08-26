package amf.core.client.platform.resource

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.{ClasspathResourceLoader => ScalaClassResourceLoader}
import amf.core.internal.remote.FutureConverter.converters

import java.util.concurrent.CompletableFuture

case class ClasspathResourceLoader() extends ResourceLoader {

  override def fetch(resource: String): CompletableFuture[Content] = ScalaClassResourceLoader.fetch(resource).asJava

  override def accepts(resource: String): Boolean = ScalaClassResourceLoader.accepts(resource)
}
