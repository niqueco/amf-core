package amf.core.client.scala.model.domain

import amf.core.client.scala.model.domain.extensions.DomainExtension

trait CustomizableElement {

  def customDomainProperties: Seq[DomainExtension]
  def withCustomDomainProperties(extensions: Seq[DomainExtension]): this.type
  def withCustomDomainProperty(extensions: DomainExtension): this.type
}
