package amf.core.plugins

import scala.concurrent.Future

trait AMFPlugin {

  val ID: String

  def dependencies(): Seq[AMFPlugin]
  def init(): Future[AMFPlugin]
}
