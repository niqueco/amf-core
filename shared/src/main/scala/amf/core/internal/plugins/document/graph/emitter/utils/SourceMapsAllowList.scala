package amf.core.internal.plugins.document.graph.emitter.utils

import amf.core.internal.annotations.{DeclaredElement, TrackedElement}

object SourceMapsAllowList {
  def apply(): List[String] = List(TrackedElement.name, DeclaredElement.name)
}
