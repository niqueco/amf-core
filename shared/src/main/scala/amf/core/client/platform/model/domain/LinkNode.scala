package amf.core.client.platform.model.domain

import amf.core.client.platform.model.StrField
import amf.core.client.platform.model.domain.common.HasDescription
import amf.core.client.scala.model.domain.{LinkNode => InternalLinkNode}
import amf.core.internal.convert.CoreClientConverters._

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
case class LinkNode(override private[amf] val _internal: InternalLinkNode) extends DataNode with HasDescription {

  @JSExportTopLevel("LinkNode")
  def this() = this(InternalLinkNode())

  @JSExportTopLevel("LinkNode")
  def this(alias: String, value: String) = this(InternalLinkNode(alias, value))

  def link: StrField  = _internal.link
  def alias: StrField = _internal.alias

  /** Set link value. */
  def withLink(link: String): this.type = {
    _internal.withLink(link)
    this
  }

  /** Set alias value. */
  def withAlias(alias: String): this.type = {
    _internal.withAlias(alias)
    this
  }
}
