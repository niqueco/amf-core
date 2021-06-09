package amf.core.client.scala.model.domain.templates

import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.domain.templates.AbstractDeclarationModel._
import amf.core.client.scala.model.StrField
import amf.core.client.scala.model.domain.{DataNode, DomainElement, Linkable, NamedDomainElement}
import amf.core.internal.parser.domain.Fields
import amf.core.internal.utils.AmfStrings
import amf.core.internal.parser.domain.{Annotations, Fields}

abstract class AbstractDeclaration(fields: Fields, annotations: Annotations)
    extends DomainElement
    with Linkable
    with NamedDomainElement {

  def description: StrField    = fields.field(Description)
  def dataNode: DataNode       = fields.field(DataNode)
  def variables: Seq[StrField] = fields.field(Variables)

  def withDataNode(dataNode: DataNode): this.type      = set(DataNode, dataNode)
  def withVariables(variables: Seq[String]): this.type = set(Variables, variables)
  def withDescription(description: String): this.type  = set(Description, description)

  protected def declarationComponent: String
  override def componentId: String =
    "/" + declarationComponent + "/" + name.option().getOrElse("default-abstract").urlComponentEncoded
  override def nameField: Field = Name
}
