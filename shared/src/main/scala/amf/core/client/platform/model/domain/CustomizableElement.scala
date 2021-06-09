package amf.core.client.platform.model.domain

import amf.core.internal.convert.CoreClientConverters.ClientList

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait CustomizableElement {

  def customDomainProperties: ClientList[DomainExtension]
  def withCustomDomainProperties(extensions: ClientList[DomainExtension]): this.type
}
