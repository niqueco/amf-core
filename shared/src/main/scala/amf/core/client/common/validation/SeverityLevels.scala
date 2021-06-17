package amf.core.client.common.validation

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("SeverityLevels")
object SeverityLevels {
  val WARNING   = "Warning"
  val INFO      = "Info"
  val VIOLATION = "Violation"

  def unapply(arg: String): String = arg match {
    case WARNING => WARNING
    case INFO    => INFO
    case _       => VIOLATION
  }
}
