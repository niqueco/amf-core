package amf.core.client.scala.config

import amf.core.client.scala.model.document.BaseUnit

import scala.concurrent.Future

trait UnitCache {
  /** Fetch specified reference and return associated cached reference if exists. */
  def fetch(url: String): Future[CachedReference]
}

case class CachedReference(url: String, content: BaseUnit, resolved: Boolean)
