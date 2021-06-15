package amf.core.client.platform.validation

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.execution.BaseExecutionEnvironment
import amf.core.client.platform.model.document.PayloadFragment
import amf.core.client.scala.validation.payload.{PayloadValidator => InternalPayloadValidator}
import scala.concurrent.ExecutionContext
import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
class PayloadValidator(private[amf] val _internal: InternalPayloadValidator,
                       private val exec: BaseExecutionEnvironment = platform.defaultExecutionEnvironment) {

  private implicit val executionContext: ExecutionContext = exec.executionContext

  def isValid(mediaType: String, payload: String): ClientFuture[Boolean] =
    _internal.isValid(mediaType, payload).asClient
  def validate(mediaType: String, payload: String): ClientFuture[AMFValidationReport] =
    _internal.validate(mediaType, payload).asClient
  def validate(payloadFragment: PayloadFragment): ClientFuture[AMFValidationReport] =
    _internal.validate(payloadFragment).asClient

  def syncValidate(mediaType: String, payload: String): AMFValidationReport =
    _internal.syncValidate(mediaType, payload)
}
