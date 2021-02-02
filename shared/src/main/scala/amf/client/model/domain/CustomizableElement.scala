package amf.client.model.domain

import amf.client.convert.CoreClientConverters.ClientList

trait CustomizableElement {

  def customDomainProperties: ClientList[DomainExtension]
  def withCustomDomainProperties(extensions: ClientList[DomainExtension]): this.type
}
