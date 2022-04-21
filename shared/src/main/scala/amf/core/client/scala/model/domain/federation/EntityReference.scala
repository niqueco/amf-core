package amf.core.client.scala.model.domain.federation

import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.metamodel.Obj
import amf.core.internal.metamodel.domain.federation.EntityReferenceModel
import amf.core.internal.parser.domain.{Annotations, Fields}

case class EntityReference(override val fields: Fields, override val annotations: Annotations) extends DomainElement {
  override def meta: Obj = EntityReferenceModel

  /** Value , path + field value that is used to compose the id when the object its adopted */
  override private[amf] def componentId = "/entity-reference/"
}
