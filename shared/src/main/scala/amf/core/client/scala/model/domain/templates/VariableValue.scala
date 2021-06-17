package amf.core.client.scala.model.domain.templates

import amf.core.internal.metamodel.{Field, Obj}
import amf.core.internal.metamodel.domain.templates.VariableValueModel
import amf.core.internal.metamodel.domain.templates.VariableValueModel.{Name, Value}
import amf.core.client.scala.model.domain.{DataNode, DomainElement, NamedDomainElement}
import amf.core.internal.parser.domain.Fields
import org.yaml.model.YPart
import amf.core.internal.utils.AmfStrings
import amf.core.internal.parser.domain.{Annotations, Fields}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

case class VariableValue(fields: Fields, annotations: Annotations) extends DomainElement with NamedDomainElement {

  def value: DataNode = fields.field(Value)

  def withValue(value: DataNode): this.type = set(Value, value)

  override def meta: VariableValueModel.type = VariableValueModel

  /** Value , path + field value that is used to compose the id when the object its adopted */
  override def componentId: String = "/" + name.option().getOrElse("default-variable").urlComponentEncoded

  override protected def nameField: Field = Name
}

object VariableValue {

  def apply(): VariableValue = apply(Annotations())

  def apply(ast: YPart): VariableValue = apply(Annotations(ast))

  def apply(annotations: Annotations): VariableValue = apply(Fields(), annotations)
}

@JSExportTopLevel("Variable")
@JSExportAll
case class Variable(name: String, value: DataNode)
