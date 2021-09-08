package amf.core.client.platform.model.document

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.model.domain.DomainElement
import amf.core.client.scala.model.document.{Document => InternalDocument}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/**
  * A [[Document]] is a parsing Unit that encodes a stand-alone DomainElement and can include references to other
  * DomainElements that reference from the encoded DomainElement
  */
@JSExportAll
class Document(private[amf] val _internal: InternalDocument) extends BaseUnit with EncodesModel with DeclaresModel {

  @JSExportTopLevel("Document")
  def this() = this(InternalDocument())

  @JSExportTopLevel("Document")
  def this(encoding: DomainElement) = this(InternalDocument().withEncodes(encoding))
}
