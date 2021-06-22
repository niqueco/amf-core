package amf.core.client.platform.config

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.scala.config.{RenderOptions => InternalRenderOptions}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
case class RenderOptions(private[amf] val _internal: InternalRenderOptions) {

  @JSExportTopLevel("RenderOptions")
  def this() = this(InternalRenderOptions())

  /** Pretty print the graph. */
  def withPrettyPrint: RenderOptions = _internal.withPrettyPrint

  /** Not Pretty print the graph. */
  def withoutPrettyPrint: RenderOptions = _internal.withoutPrettyPrint

  /** Include source maps when rendering to graph. */
  def withSourceMaps: RenderOptions = _internal.withSourceMaps

  /** Include source maps when rendering to graph. */
  def withoutSourceMaps: RenderOptions = _internal.withoutSourceMaps

  def withCompactUris: RenderOptions = _internal.withCompactUris

  def withoutCompactUris: RenderOptions = _internal.withoutCompactUris

  /**
    * Emit specific AMF JSON-LD serialization
    *
    * @return
    */
  def withoutAmfJsonLdSerialization: RenderOptions = _internal.withoutAmfJsonLdSerialization

  /**
    * Emit regular JSON-LD serialization
    *
    * @return
    */
  def withAmfJsonLdSerialization: RenderOptions = _internal.withAmfJsonLdSerialization

  def withNodeIds: RenderOptions = _internal.withNodeIds

  def withShapeRenderOptions(s: ShapeRenderOptions): RenderOptions = _internal.withShapeRenderOptions(s)

  def isWithCompactUris: Boolean             = _internal.compactUris
  def isWithSourceMaps: Boolean              = _internal.sources
  def isAmfJsonLdSerialization: Boolean      = _internal.amfJsonLdSerialization
  def isPrettyPrint: Boolean                 = _internal.prettyPrint
  def isEmitNodeIds: Boolean                 = _internal.emitNodeIds
  def shapeRenderOptions: ShapeRenderOptions = _internal.shapeRenderOptions
}
