package amf.client.remod.amfcore.registry

import amf.core.model.domain.DomainElement

case class EntitiesRegistry(domainEntities: Map[String, DomainElement], wrappersRegistry: Map[String, DomainElement]) {}

object EntitiesRegistry {
  val empty = EntitiesRegistry(Map.empty, Map.empty)
}
