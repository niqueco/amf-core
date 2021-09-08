package amf.core.client.platform.model.document

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.model.domain.{CustomizableElement, DomainExtension}
import amf.core.client.scala.model.document.{Module => InternalModule}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** Units containing abstract fragments that can be referenced from other fragments */
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
