package amf.core.client.platform.config

import amf.core.client.common.render.JSONSchemaVersion
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

  /** Remove documentation info as examples, descriptions, display names, etc. (only supported for json schema rendering) */
  def withoutDocumentation: RenderOptions = _internal.withoutDocumentation

  /** Render shapes without extracting common types to definitions (feature is enable by default for OAS and json schema) */
  def withoutCompactedEmission: RenderOptions = _internal.withoutCompactedEmission

  /** Render shapes with specific json schema version (supported for json schema rendering) */
  def withSchemaVersion(version: JSONSchemaVersion): RenderOptions = _internal.withSchemaVersion(version)

  def isWithDocumentation: Boolean     = _internal.isWithDocumentation
  def isWithCompactedEmission: Boolean = _internal.isWithCompactedEmission
  def schemaVersion: JSONSchemaVersion = _internal.schemaVersion

  def isWithCompactUris: Boolean        = _internal.compactUris
  def isWithSourceMaps: Boolean         = _internal.sources
  def isAmfJsonLdSerialization: Boolean = _internal.amfJsonLdSerialization
  def isPrettyPrint: Boolean            = _internal.prettyPrint
  def isEmitNodeIds: Boolean            = _internal.emitNodeIds
}
