package amf.core.client.platform.execution

import java.util.concurrent.ScheduledExecutorService
import amf.core.client.scala.execution.{ExecutionEnvironment => InternalExecutionEnvironment}
import amf.core.internal.execution.ExecutionContextBuilder

case class ExecutionEnvironment(override private[amf] val _internal: InternalExecutionEnvironment)
    extends BaseExecutionEnvironment(_internal) {

  def this() = this(InternalExecutionEnvironment())

  def this(scheduler: ScheduledExecutorService) =
    this(InternalExecutionEnvironment(ExecutionContextBuilder.buildExecutionContext(scheduler)))
}

object DefaultExecutionEnvironment {
  def apply(): ExecutionEnvironment = new ExecutionEnvironment()
}
