package amf.core.internal.reference

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.reference.{UnitCache => ClientUnitCache}
import amf.core.client.scala.config.{CachedReference, UnitCache}

import scala.concurrent.{ExecutionContext, Future}

/** Adapts a client ReferenceResolver to an internal one. */
case class UnitCacheAdapter(private[amf] val adaptee: ClientUnitCache)(implicit executionContext: ExecutionContext)
    extends UnitCache {

  override def fetch(url: String): Future[CachedReference] = adaptee.fetch(url).asInternal
}
