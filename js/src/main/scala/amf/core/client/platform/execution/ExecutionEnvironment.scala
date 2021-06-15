package amf.core.client.platform.execution

import amf.core.client.scala.execution.{ExecutionEnvironment => InternalExecutionEnvironment}

import scala.scalajs.js.annotation.{JSExport, JSExportAll, JSExportTopLevel}

@JSExportAll
case class ExecutionEnvironment(override private[amf] val _internal: InternalExecutionEnvironment)
    extends BaseExecutionEnvironment(_internal) {
  @JSExportTopLevel("client.execution.ExecutionEnvironment")
  def this() = this(InternalExecutionEnvironment())
}

@JSExportTopLevel("client.DefaultExecutionEnvironment")
object DefaultExecutionEnvironment {
  @JSExport("apply")
  def apply(): ExecutionEnvironment = new ExecutionEnvironment()
}
