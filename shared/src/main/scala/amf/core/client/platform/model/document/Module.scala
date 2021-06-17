package amf.core.client.platform.model.document

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.model.domain.{CustomizableElement, DomainExtension}
import amf.core.client.scala.model.document.{Module => InternalModule}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/**
  * Module model class
  */
@JSExportAll
case class Module(private[amf] val _internal: InternalModule)
    extends BaseUnit
    with DeclaresModel
    with CustomizableElement {

  @JSExportTopLevel("Module")
  def this() = this(InternalModule())

  def customDomainProperties: ClientList[DomainExtension] = _internal.customDomainProperties.asClient

  def withCustomDomainProperties(extensions: ClientList[DomainExtension]): this.type = {
    _internal.withCustomDomainProperties(extensions.asInternal)
    this
  }
}
