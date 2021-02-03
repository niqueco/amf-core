package amf.client.model.domain

import amf.client.convert.CoreClientConverters.ClientList

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait CustomizableElement {

  def customDomainProperties: ClientList[DomainExtension]
  def withCustomDomainProperties(extensions: ClientList[DomainExtension]): this.type
}
