package amf.core.client.scala.model

import amf.core.internal.parser.domain.Annotations

trait Annotable {

  /** Return annotations. */
  def annotations(): Annotations
}
