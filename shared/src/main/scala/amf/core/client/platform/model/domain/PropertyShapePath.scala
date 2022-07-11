package amf.core.client.platform.model.domain

import amf.core.client.scala.model.domain.extensions.{PropertyShapePath => InternalPropertyShapePath}
import amf.core.internal.convert.CoreClientConverters._

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
case class PropertyShapePath(override private[amf] val _internal: InternalPropertyShapePath) extends DomainElement {

  def path: ClientList[PropertyShape] = _internal.path.asClient

  def withPath(path: ClientList[PropertyShape]): this.type = {
    _internal.withPath(path.asInternal)
    this
  }

  @JSExportTopLevel("PropertyShapePath")
  def this() = this(InternalPropertyShapePath())

}
