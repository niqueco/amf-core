package amf.core.client.scala.model.domain.federation

import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.metamodel.Obj
import amf.core.internal.metamodel.domain.federation.EntityKeysModel
import amf.core.internal.metamodel.domain.federation.EntityKeysModel.PrimaryKeys
import amf.core.internal.parser.domain.{Annotations, Fields}

case class EntityKeys(override val fields: Fields, override val annotations: Annotations) extends DomainElement {
  override def meta: Obj = EntityKeysModel

  def primaryKeys = fields(PrimaryKeys)

  /** Value , path + field value that is used to compose the id when the object its adopted */
  override private[amf] def componentId = "/keys/"
}

object EntityKeys {
  def apply() = new EntityKeys(Fields(), Annotations())
}
