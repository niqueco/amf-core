package amf.core.client.platform.model.document

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.model.AmfObjectWrapper
import amf.core.client.platform.model.domain.DomainElement
import amf.core.client.scala.model.document.{DeclaresModel => InternalDeclaresModel}

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait DeclaresModel extends AmfObjectWrapper {

  override private[amf] val _internal: InternalDeclaresModel

  /** Declared DomainElements that can be re-used from other documents. */
  def declares: ClientList[DomainElement] = _internal.declares.asClient

  def withDeclaredElement(declared: DomainElement): this.type = {
    _internal.withDeclaredElement(declared)
    this
  }

  def withDeclares(declares: ClientList[DomainElement]): this.type = {
    _internal.withDeclares(declares.asInternal)
    this
  }
}
