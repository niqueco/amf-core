package amf.client.remod.amfcore.registry

import amf.core.model.domain.DomainElement

private[remod] case class EntitiesRegistry(domainEntities: Map[String, DomainElement], wrappersRegistry: Map[String, DomainElement]) {}

private[remod] object EntitiesRegistry {
  val empty = EntitiesRegistry(Map.empty, Map.empty)
}
