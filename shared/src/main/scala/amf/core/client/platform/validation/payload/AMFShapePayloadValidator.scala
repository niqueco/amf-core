package amf.core.client.platform.validation.payload

import amf.core.client.platform.model.document.PayloadFragment
import amf.core.client.platform.validation.AMFValidationReport
import amf.core.internal.convert.CoreClientConverters._

import java.io.InputStream
import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait AMFShapePayloadValidator {

  def validate(payload: String): ClientFuture[AMFValidationReport]
  def validate(payloadFragment: PayloadFragment): ClientFuture[AMFValidationReport]
  def syncValidate(payload: String): AMFValidationReport
  def syncValidate(stream: InputStream): AMFValidationReport
}
