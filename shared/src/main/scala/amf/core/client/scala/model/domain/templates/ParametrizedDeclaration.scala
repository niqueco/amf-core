package amf.core.client.scala.model.domain.templates

import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.domain.templates.ParametrizedDeclarationModel._
import amf.core.client.scala.model.domain.NamedDomainElement
import amf.core.internal.parser.domain.Fields
import amf.core.internal.utils.AmfStrings
import amf.core.internal.parser.domain.{Annotations, Fields}

abstract class ParametrizedDeclaration(fields: Fields, annotations: Annotations) extends NamedDomainElement {

  def target: AbstractDeclaration   = fields.field(Target)
  def variables: Seq[VariableValue] = fields.field(Variables)

  /** Set the target property. */
  def withTarget(target: AbstractDeclaration): this.type = set(Target, target)

  /** Set variables property. */
  def withVariables(variables: Seq[VariableValue]): this.type = setArray(Variables, variables)

  override def componentId: String =
    "/" + name.option().getOrElse("default-parametrized").urlComponentEncoded

  override def nameField: Field = Name
}
