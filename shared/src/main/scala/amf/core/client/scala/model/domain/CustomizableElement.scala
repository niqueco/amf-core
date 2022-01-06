package amf.core.client.scala.model.domain

import amf.core.client.scala.model.domain.extensions.DomainExtension
import amf.core.internal.metamodel.Type

trait CustomizableElement {

  def typeIris: Seq[String]
  def customDomainProperties: Seq[DomainExtension]
  def withCustomDomainProperties(extensions: Seq[DomainExtension]): this.type
  def withCustomDomainProperty(extensions: DomainExtension): this.type
}

object GhostCustomizableElement {
  def apply(typeIris: Seq[String]) = new GhostCustomizableElement(typeIris)
  def apply(model: Type)           = new GhostCustomizableElement(model.typeIris)
  def apply()                      = new GhostCustomizableElement(Seq.empty)
}

case class GhostCustomizableElement(typeIris: Seq[String]) extends CustomizableElement {
  override def customDomainProperties: Seq[DomainExtension]                                                     = Seq.empty
  override def withCustomDomainProperties(extensions: Seq[DomainExtension]): GhostCustomizableElement.this.type = this
  override def withCustomDomainProperty(extensions: DomainExtension): GhostCustomizableElement.this.type        = this
}
