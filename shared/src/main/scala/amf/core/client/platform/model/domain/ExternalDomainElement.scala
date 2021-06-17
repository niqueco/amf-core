package amf.core.client.platform.model.domain

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.model.StrField
import amf.core.client.scala.model.domain.{ExternalDomainElement => InternalExternalDomainElement}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
case class ExternalDomainElement(private[amf] val _internal: InternalExternalDomainElement) extends DomainElement {

  @JSExportTopLevel("ExternalDomainElement")
  def this() = this(InternalExternalDomainElement())

  def raw: StrField       = _internal.raw
  def mediaType: StrField = _internal.mediaType

  def withRaw(raw: String): this.type = {
    _internal.withRaw(raw)
    this
  }

  def withMediaType(mediaType: String): this.type = {
    _internal.withMediaType(mediaType)
    this
  }
}
