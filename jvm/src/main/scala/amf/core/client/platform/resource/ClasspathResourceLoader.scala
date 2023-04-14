package amf.core.client.platform.resource

import amf.core.client.common.remote.Content
import amf.core.client.scala.resource.{ClasspathResourceLoader => ScalaClassResourceLoader}
import scala.jdk.FutureConverters._

import java.util.concurrent.CompletableFuture

case class ClasspathResourceLoader() extends ResourceLoader {

  override def fetch(resource: String): CompletableFuture[Content] =
    ScalaClassResourceLoader.fetch(resource).asJava.toCompletableFuture

  override def accepts(resource: String): Boolean = ScalaClassResourceLoader.accepts(resource)
}
