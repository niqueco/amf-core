package amf.core.client.platform.model.domain

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.model.StrField
import amf.core.client.scala.model.domain.{RecursiveShape => InternalRecursiveShape}
import amf.core.internal.unsafe.PlatformSecrets

import scala.scalajs.js.annotation.JSExport

case class RecursiveShape(private[amf] override val _internal: InternalRecursiveShape)
    extends Shape
    with PlatformSecrets {

  @JSExport
  def fixpoint: StrField = _internal.fixpoint

  @JSExport
  def withFixPoint(shapeId: String): this.type = {
    _internal.withFixPoint(shapeId)
    this
  }

  @JSExport
  override def linkCopy(): Linkable = throw new Exception("Recursive shape cannot be linked")
}
