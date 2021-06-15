package amf.core.client.platform.plugins

import scala.scalajs.js.annotation.JSExportAll
import amf.core.internal.convert.CoreClientConverters._

@JSExportAll
trait ClientAMFPlugin {
  val ID: String

  def dependencies(): ClientList[ClientAMFPlugin]
  def init(): ClientFuture[ClientAMFPlugin]
}
