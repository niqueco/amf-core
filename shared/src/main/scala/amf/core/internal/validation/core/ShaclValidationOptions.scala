package amf.core.internal.validation.core

import amf.core.client.scala.config.{AMFEventListener, RenderOptions}
import amf.core.client.common.validation.{AMFStyle, MessageStyle}
import amf.core.internal.metamodel.Field

class ShaclValidationOptions(val listeners: Seq[AMFEventListener] = Seq.empty) {
  val filterFields: Field => Boolean = (_: Field) => false
  var messageStyle: MessageStyle     = AMFStyle

  def toRenderOptions: RenderOptions = RenderOptions().withValidation.withFilterFieldsFunc(filterFields)

  def withMessageStyle(style: MessageStyle): ShaclValidationOptions = {
    messageStyle = style
    this
  }
}

object DefaultShaclValidationOptions extends ShaclValidationOptions {}
