package amf.core.model.domain.extensions

import amf.core.metamodel.domain.extensions.CustomDomainPropertyModel
import amf.core.metamodel.domain.extensions.CustomDomainPropertyModel._
import amf.core.model.domain.{DomainElement, Linkable, NamedDomainElement, Shape}
import amf.core.parser.{Annotations, Fields}
import amf.core.utils._
import org.yaml.model.YPart

case class CustomDomainProperty(fields: Fields, annotations: Annotations)
    extends DomainElement
    with Linkable
    with NamedDomainElement {

  def name: String        = fields(Name)
  def displayName: String = fields(DisplayName)
  def description: String = fields(Description)
  def domain: Seq[String] = fields(Domain)
  def schema: Shape       = fields(Schema)

  def withName(name: String): this.type               = set(Name, name)
  def withDisplayName(displayName: String): this.type = set(DisplayName, displayName)
  def withDescription(description: String): this.type = set(Description, description)
  def withDomain(domain: Seq[String]): this.type      = set(Domain, domain)
  def withSchema(schema: Shape): this.type            = set(Schema, schema)

  override def adopted(parent: String): this.type =
    if (Option(this.id).isEmpty) { withId(parent + "/" + name.urlEncoded) } else { this }

  override def linkCopy(): CustomDomainProperty = CustomDomainProperty().withId(id)

  override def meta = CustomDomainPropertyModel
}

object CustomDomainProperty {
  def apply(): CustomDomainProperty = apply(Annotations())

  def apply(ast: YPart): CustomDomainProperty = apply(Annotations(ast))

  def apply(annotations: Annotations): CustomDomainProperty = CustomDomainProperty(Fields(), annotations)
}
