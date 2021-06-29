package amf.core.client.scala.config

import amf.core.client.common.render.{JSONSchemaVersion, JSONSchemaVersions}
import amf.core.internal.metamodel.Field
import amf.core.internal.plugins.document.graph._

/**
  * Immutable implementation of render options
  */
case class RenderOptions(
    sources: Boolean = false,
    compactUris: Boolean = false,
    rawSourceMaps: Boolean = false,
    validating: Boolean = false,
    filterFields: Field => Boolean = (_: Field) => false,
    amfJsonLdSerialization: Boolean = true,
    useJsonLdEmitter: Boolean = false,
    private[amf] val flattenedJsonLd: Boolean = true,
    prettyPrint: Boolean = false,
    emitNodeIds: Boolean = false,
    documentation: Boolean = true,
    compactedEmission: Boolean = true,
    emitWarningForUnsupportedValidationFacets: Boolean = false,
    schema: JSONSchemaVersion = JSONSchemaVersions.Unspecified,
) {

  /** Include PrettyPrint when rendering to graph. */
  def withPrettyPrint: RenderOptions = copy(prettyPrint = true)

  /** Exclude PrettyPrint when rendering to graph. */
  def withoutPrettyPrint: RenderOptions = copy(prettyPrint = true)

  /** Include source maps when rendering to graph. */
  def withSourceMaps: RenderOptions = copy(sources = true)

  /** Exclude source maps when rendering to graph. */
  def withoutSourceMaps: RenderOptions = copy(sources = false)

  /** Include CompactUris when rendering to graph. */
  def withCompactUris: RenderOptions = copy(compactUris = true)

  /** Exclude CompactUris when rendering to graph. */
  def withoutCompactUris: RenderOptions = copy(compactUris = false)

  /** Include RawSourceMaps when rendering to graph. */
  def withRawSourceMaps: RenderOptions = copy(rawSourceMaps = true)

  /** Exclude RawSourceMaps when rendering to graph. */
  def withoutRawSourceMaps: RenderOptions = copy(rawSourceMaps = false)

  /** Include Validation when rendering to graph. */
  def withValidation: RenderOptions = copy(validating = true)

  /** Exclude Validation when rendering to graph. */
  def withoutValidation: RenderOptions = copy(validating = false)

  /** Include Node IDs when rendering to graph. */
  def withNodeIds: RenderOptions = copy(emitNodeIds = true)

  /** Exclude Node IDs when rendering to graph. */
  def withoutNodeIds: RenderOptions = copy(emitNodeIds = false)

  /** Apply function to filter fields when rendering to graph. */
  def withFilterFieldsFunc(f: Field => Boolean): RenderOptions = copy(filterFields = f)

  /** Include AmfJsonLdSerialization when rendering to graph. */
  def withAmfJsonLdSerialization: RenderOptions = copy(amfJsonLdSerialization = true)

  /** Exclude AmfJsonLdSerialization when rendering to graph. */
  def withoutAmfJsonLdSerialization: RenderOptions = copy(amfJsonLdSerialization = false)

  /** Include FlattenedJsonLd when rendering to graph. */
  private[amf] def withFlattenedJsonLd: RenderOptions = copy(flattenedJsonLd = true)

  /** Exclude FlattenedJsonLd when rendering to graph. */
  private[amf] def withoutFlattenedJsonLd: RenderOptions = copy(flattenedJsonLd = false)

  private[amf] def isFlattenedJsonLd: Boolean = flattenedJsonLd

  /** Remove documentation info as examples, descriptions, display names, etc. (only supported for json schema rendering) */
  def withoutDocumentation: RenderOptions = copy(documentation = false)

  /** Render shapes without extracting common types to definitions (feature is enable by default for OAS and json schema) */
  def withoutCompactedEmission: RenderOptions = copy(compactedEmission = false)

  def withEmitWarningForUnsupportedValidationFacets(value: Boolean): RenderOptions =
    copy(emitWarningForUnsupportedValidationFacets = value)

  /** Render shapes with specific json schema version (supported for json schema rendering) */
  def withSchemaVersion(version: JSONSchemaVersion): RenderOptions = copy(schema = version)

  def isWithDocumentation: Boolean                             = documentation
  def isWithCompactedEmission: Boolean                         = compactedEmission
  def shouldEmitWarningForUnsupportedValidationFacets: Boolean = emitWarningForUnsupportedValidationFacets
  def schemaVersion: JSONSchemaVersion                         = schema

  def isCompactUris: Boolean             = compactUris
  def isWithSourceMaps: Boolean          = sources
  def isWithRawSourceMaps: Boolean       = rawSourceMaps
  def isAmfJsonLdSerialization: Boolean  = amfJsonLdSerialization
  def isValidation: Boolean              = validating
  def renderField(field: Field): Boolean = !filterFields(field)
  def isPrettyPrint: Boolean             = prettyPrint
  def isEmitNodeIds: Boolean             = emitNodeIds

  // TODO: remove when embeddedform is deleted
  private[amf] def toGraphSerialization: GraphSerialization = {
    if (isAmfJsonLdSerialization) {
      if (isFlattenedJsonLd) {
        JsonLdSerialization(FlattenedForm)
      } else {
        JsonLdSerialization(EmbeddedForm)
      }
    } else {
      RdfSerialization()
    }
  }
}
