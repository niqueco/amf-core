package amf.client.model.document

import amf.client.convert.CoreClientConverters._
import amf.client.model.domain.{CustomizableElement, DomainExtension}
import amf.core.model.document.{Module => InternalModule}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/**
  * Module model class
  */
@JSExportAll
@JSExportTopLevel("model.document.Module")
case class Module(private[amf] val _internal: InternalModule) extends BaseUnit with DeclaresModel with CustomizableElement{

  @JSExportTopLevel("model.document.Module")
  def this() = this(InternalModule())

  def customDomainProperties: ClientList[DomainExtension] = _internal.customDomainProperties.asClient

  def withCustomDomainProperties(extensions: ClientList[DomainExtension]): this.type = {
    _internal.withCustomDomainProperties(extensions.asInternal)
    this
  }
}
