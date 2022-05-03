package amf.core.client.scala.model.domain

import amf.core.internal.metamodel.domain.DomainElementModel._
import amf.core.client.scala.model.BoolField
import amf.core.client.scala.model.domain.extensions.DomainExtension

/** Internal model for any domain element
  */
trait DomainElement extends AmfObject with CustomizableElement {

  override def typeIris: Seq[String] = meta.typeIris

  def customDomainProperties: Seq[DomainExtension] = fields.field(CustomDomainProperties)
  def extend: Seq[DomainElement]                   = fields.field(Extends)

  def withCustomDomainProperties(extensions: Seq[DomainExtension]): this.type =
    setArray(CustomDomainProperties, extensions)

  def withCustomDomainProperty(extensions: DomainExtension): this.type =
    add(CustomDomainProperties, extensions)

  def isExternalLink: BoolField                                         = fields.field(IsExternalLink)
  def withIsExternalLink(isReference: Boolean): DomainElement.this.type = set(IsExternalLink, isReference)

  def withExtends(extend: Seq[DomainElement]): this.type = setArray(Extends, extend)
  lazy val graph: Graph                                  = Graph(this)
}
