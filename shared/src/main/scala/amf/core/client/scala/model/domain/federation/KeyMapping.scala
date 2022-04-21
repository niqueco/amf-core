package amf.core.client.scala.model.domain.federation

import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.metamodel.Obj
import amf.core.internal.metamodel.domain.federation.KeyMappingModel
import amf.core.internal.parser.domain.{Annotations, Fields}

case class KeyMapping(override val fields: Fields, override val annotations: Annotations) extends DomainElement {
  override def meta: Obj = KeyMappingModel

  /** Value , path + field value that is used to compose the id when the object its adopted */
  override private[amf] def componentId = "/key-mapping/"
}

object KeyMapping {
  def apply() = new KeyMapping(Fields(), Annotations())
}
