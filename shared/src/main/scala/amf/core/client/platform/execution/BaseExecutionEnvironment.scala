package amf.core.client.platform.execution

import amf.core.client.scala.execution.{ExecutionEnvironment => InternalExecutionEnvironment}

import scala.concurrent.ExecutionContext

abstract class BaseExecutionEnvironment(private[amf] val _internal: InternalExecutionEnvironment) {
  def executionContext: ExecutionContext = _internal.context
}
