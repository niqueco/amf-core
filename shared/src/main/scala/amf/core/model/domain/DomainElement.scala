package amf.core.model.domain

import amf.core.metamodel.domain.DomainElementModel._
import amf.core.model.BoolField
import amf.core.model.domain.extensions.DomainExtension

/**
  * Internal model for any domain element
  */
trait DomainElement extends AmfObject with CustomizableElement {

  def extend: Seq[DomainElement]                   = fields.field(Extends)

  def isExternalLink: BoolField = fields.field(IsExternalLink)
  def withIsExternalLink(isReference: Boolean): DomainElement.this.type = set(IsExternalLink, isReference)

  def withExtends(extend: Seq[DomainElement]): this.type = setArray(Extends, extend)

  lazy val graph: Graph = Graph(this)

}
