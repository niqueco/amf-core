package amf.core.client.scala.model.domain.federation

import amf.core.client.scala.model.StrField
import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.metamodel.Obj
import amf.core.internal.metamodel.domain.federation.FieldReferenceModel
import amf.core.internal.metamodel.domain.federation.FieldReferenceModel.{Expression, Language, Target}
import amf.core.internal.parser.domain.{Annotations, Fields}
import amf.core.internal.utils.AmfStrings

case class FieldReference(override val fields: Fields, override val annotations: Annotations) extends DomainElement {
  override def meta: Obj = FieldReferenceModel

  def expression: StrField  = fields(Expression)
  def target: DomainElement = fields(Target)
  def language: StrField    = fields(Language)

  /** Value , path + field value that is used to compose the id when the object its adopted */
  override private[amf] def componentId =
    "/field-reference/" + expression.option().getOrElse("default-expression").urlComponentEncoded

}
