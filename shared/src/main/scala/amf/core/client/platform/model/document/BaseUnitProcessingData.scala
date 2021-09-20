package amf.core.client.platform.model.document

import amf.core.client.platform.model.{AmfObjectWrapper, BoolField}
import amf.core.client.scala.model.document.{BaseUnitProcessingData => InternalBaseUnitProcessingData}
import amf.core.internal.convert.CoreClientConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
class BaseUnitProcessingData(private[amf] val _internal: InternalBaseUnitProcessingData) extends AmfObjectWrapper {

  @JSExportTopLevel("BaseUnitProcessingData")
  def this() = this(InternalBaseUnitProcessingData())

  def transformed: BoolField = _internal.transformed

  def withTransformed(value: Boolean): this.type = {
    _internal.withTransformed(value)
    this
  }
}
