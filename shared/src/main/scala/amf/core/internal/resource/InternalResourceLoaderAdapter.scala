package amf.core.internal.resource

import amf.core.client.common.remote.Content
import amf.core.client.platform.resource.{ResourceLoader => ClientResourceLoader}
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.convert.CoreClientConverters._

import scala.concurrent.{ExecutionContext, Future}

/** Adapts a client ResourceLoader to an internal one. */
case class InternalResourceLoaderAdapter(private[amf] val adaptee: ClientResourceLoader)(
    implicit executionContext: ExecutionContext)
    extends ResourceLoader {

  override def fetch(resource: String): Future[Content] = adaptee.fetch(resource).asInternal

  override def accepts(resource: String): Boolean = adaptee.accepts(resource)
}

case class ClientResourceLoaderAdapter(private[amf] val adaptee: ResourceLoader)(
    implicit executionContext: ExecutionContext)
    extends ClientResourceLoader {

  override def fetch(resource: String): ClientFuture[Content] = adaptee.fetch(resource).asClient

  override def accepts(resource: String): Boolean = adaptee.accepts(resource)
}
