package amf.core.client.platform.reference

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.model.document.BaseUnit
import amf.core.client.scala.config
import amf.core.client.scala.config.{CachedReference => InternalCachedReference}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
trait UnitCache {

  /** Fetch specified reference and return associated [CachedReference]. */
  /** If the resource not exists, you should return a future failed with an ResourceNotFound exception. */
  def fetch(url: String): ClientFuture[CachedReference]
}

@JSExportAll
case class CachedReference private[amf] (private[amf] val _internal: config.CachedReference) {

  @JSExportTopLevel("CachedReference")
  def this(url: String, content: BaseUnit) =
    this(InternalCachedReference(url, content._internal))

  def url: String       = _internal.url
  def content: BaseUnit = _internal.content
}
