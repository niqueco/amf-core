package amf.core.client.platform.model

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
trait Annotable {

  /** Return annotations. */
  def annotations(): Annotations
}
