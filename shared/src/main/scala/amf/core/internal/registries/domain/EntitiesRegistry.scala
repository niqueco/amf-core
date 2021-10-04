package amf.core.internal.registries.domain

import amf.core.client.scala.model.domain.{AnnotationGraphLoader, DomainElement}
import amf.core.internal.metamodel.ModelDefaultBuilder

private[amf] case class EntitiesRegistry(domainEntities: Map[String, ModelDefaultBuilder],
                                         serializableAnnotations: Map[String, AnnotationGraphLoader],
                                         extensions: Seq[DomainElement]) {

  def withEntities(entities: Map[String, ModelDefaultBuilder]): EntitiesRegistry =
    copy(domainEntities = domainEntities ++ entities)

  def withAnnotations(annotations: Map[String, AnnotationGraphLoader]): EntitiesRegistry =
    copy(serializableAnnotations = serializableAnnotations ++ annotations)

  def withExtensions(extensions: Seq[DomainElement]): EntitiesRegistry =
    copy(extensions = this.extensions ++ extensions)

  private[amf] def removeAllEntities(): EntitiesRegistry = copy(domainEntities = Map.empty)

  private[amf] def findType(`type`: String): Option[ModelDefaultBuilder] = domainEntities.get(`type`)

  private[amf] def findAnnotation(annotationID: String): Option[AnnotationGraphLoader] =
    serializableAnnotations.get(annotationID)

}

private[amf] object EntitiesRegistry {
  val empty: EntitiesRegistry = EntitiesRegistry(Map.empty, Map.empty, Seq.empty)
}
