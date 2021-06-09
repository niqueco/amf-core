package amf.core.internal.plugins.render

import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.plugins.AMFPlugin
import org.yaml.builder.DocBuilder

object AMFRenderPlugin {
  val APPLICATION_YAML = "application/yaml"
  val APPLICATION_JSON = "application/json"
}

trait AMFRenderPlugin extends AMFPlugin[RenderInfo] {
  def defaultSyntax(): String

  def emit[T](unit: BaseUnit, builder: DocBuilder[T], renderConfiguration: RenderConfiguration): Boolean

  def mediaTypes: Seq[String]
}
