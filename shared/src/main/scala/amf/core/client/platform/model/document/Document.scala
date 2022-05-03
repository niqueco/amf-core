package amf.core.client.platform.model.document

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.model.domain.DomainElement
import amf.core.client.scala.model.document.{Document => InternalDocument}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** A [[Document]] is a parsing Unit that encodes a DomainElement. The encoded DomainElement can reference other
  * DomainElements.
  */
@JSExportAll
class Document(private[amf] val _internal: InternalDocument) extends BaseUnit with EncodesModel with DeclaresModel {

  @JSExportTopLevel("Document")
  def this() = this(InternalDocument())

  @JSExportTopLevel("Document")
  def this(encoding: DomainElement) = this(InternalDocument().withEncodes(encoding))
}
