package amf.core.client.platform.model.domain

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.model.StrField
import amf.core.client.scala.model.domain.extensions.{DomainExtension => InternalDomainExtension}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
case class DomainExtension(private[amf] val _internal: InternalDomainExtension) extends DomainElement {

  @JSExportTopLevel("DomainExtension")
  def this() = this(InternalDomainExtension())

  def name: StrField                  = _internal.name
  def definedBy: CustomDomainProperty = _internal.definedBy
  def extension: DataNode             = _internal.extension

  def withName(name: String): this.type = {
    _internal.withName(name)
    this
  }

  def withDefinedBy(property: CustomDomainProperty): this.type = {
    _internal.withDefinedBy(property._internal)
    this
  }

  def withExtension(node: DataNode): this.type = {
    _internal.withExtension(node._internal)
    this
  }
}
