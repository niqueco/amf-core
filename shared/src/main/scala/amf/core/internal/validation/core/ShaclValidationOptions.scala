package amf.core.internal.validation.core

import amf.core.client.scala.config.RenderOptions
import amf.core.client.common.validation.{AMFStyle, MessageStyle}
import amf.core.internal.metamodel.Field

class ShaclValidationOptions() {
  val filterFields: Field => Boolean = (_: Field) => false
  var messageStyle: MessageStyle     = AMFStyle
  var level: String                  = "partial" // partial | full

  def toRenderOptions: RenderOptions = RenderOptions().withValidation.withFilterFieldsFunc(filterFields)

  def withMessageStyle(style: MessageStyle): ShaclValidationOptions = {
    messageStyle = style
    this
  }

  def withFullValidation(): ShaclValidationOptions = {
    level = "full"
    this
  }

  def withPartialValidation(): ShaclValidationOptions = {
    level = "partial"
    this
  }

  def isPartialValidation: Boolean = level == "partial"
}

object DefaultShaclValidationOptions extends ShaclValidationOptions {}
