package amf.core.client.platform.model.domain

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.scala.model.domain.{ArrayNode => InternalArrayNode}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
case class ArrayNode(override private[amf] val _internal: InternalArrayNode) extends DataNode {

  @JSExportTopLevel("model.domain.ArrayNode")
  def this() = this(InternalArrayNode())

  def members: ClientList[DataNode] = _internal.members.asClient

  def addMember(member: DataNode): this.type = {
    _internal.addMember(member._internal)
    this
  }
}
