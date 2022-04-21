package amf.core.client.scala.model.domain

import amf.core.internal.metamodel.domain.CoreTagModel
import amf.core.internal.metamodel.{Field, Obj}
import amf.core.internal.parser.domain.{Annotations, Fields}
import amf.core.internal.utils.AmfStrings

case class CoreTag(override val fields: Fields, override val annotations: Annotations) extends NamedDomainElement {
  override protected def nameField: Field = CoreTagModel.Name

  override def meta: Obj = CoreTagModel

  /** Value , path + field value that is used to compose the id when the object its adopted */
  private[amf] override def componentId: String = "/tag/" + name.option().getOrElse("default-type").urlComponentEncoded
}
object CoreTag {
  def apply() = new CoreTag(Fields(), Annotations())
}
