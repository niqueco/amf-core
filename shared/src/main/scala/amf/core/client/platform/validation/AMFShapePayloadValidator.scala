package amf.core.client.platform.validation

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.execution.BaseExecutionEnvironment
import amf.core.client.platform.model.document.PayloadFragment
import amf.core.client.scala.validation.payload.{AMFShapePayloadValidator => InternalPayloadValidator}
import scala.concurrent.ExecutionContext
import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
class AMFShapePayloadValidator(private[amf] val _internal: InternalPayloadValidator,
                               private val exec: BaseExecutionEnvironment = platform.defaultExecutionEnvironment) {

  private implicit val executionContext: ExecutionContext = exec.executionContext

  def validate(payload: String): ClientFuture[AMFValidationReport] = _internal.validate(payload).asClient
  def validate(payloadFragment: PayloadFragment): ClientFuture[AMFValidationReport] =
    _internal.validate(payloadFragment).asClient
  def syncValidate(payload: String): AMFValidationReport = _internal.syncValidate(payload)
}
