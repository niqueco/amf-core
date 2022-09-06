package amf.core.internal.plugins.document.graph.emitter.utils

trait SourceMapEmitter {
  protected def sourceMapIdFor(id: String): String = {
    if (id.endsWith("/")) {
      id + "source-map"
    } else if (id.contains("#") || id.startsWith("null")) {
      id + "/source-map"
    } else {
      id + "#/source-map"
    }
  }
}
