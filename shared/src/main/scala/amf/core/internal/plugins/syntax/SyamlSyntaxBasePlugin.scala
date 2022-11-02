package amf.core.internal.plugins.syntax

import amf.core.internal.remote.Mimes._

trait SyamlSyntaxBasePlugin {

  protected def getFormat(mediaType: String): String =
    if (mediaType.contains("json") || graphSyntax.contains(mediaType)) "json" else "yaml"

  protected def graphSyntax: Seq[String] =
    Seq(
      `application/graph`,
      "application/schemald+json"
    )
}
