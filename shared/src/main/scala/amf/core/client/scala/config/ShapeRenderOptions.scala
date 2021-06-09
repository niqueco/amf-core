package amf.core.client.scala.config

import amf.core.client.platform.config.{JSONSchemaVersion, JSONSchemaVersions}

/**
  * Immutable implementation of shape render options
  */
case class ShapeRenderOptions(
    documentation: Boolean = true,
    compactedEmission: Boolean = true,
    emitWarningForUnsupportedValidationFacets: Boolean = false,
    schema: JSONSchemaVersion = JSONSchemaVersions.UNSPECIFIED,
) {

  /** Remove documentation info as examples, descriptions, display names, etc. */
  def withoutDocumentation: ShapeRenderOptions = copy(documentation = false)

  /** Render shape without extracting common types to definitions */
  def withoutCompactedEmission: ShapeRenderOptions = copy(compactedEmission = false)

  def withEmitWarningForUnsupportedValidationFacets(value: Boolean): ShapeRenderOptions =
    copy(emitWarningForUnsupportedValidationFacets = value)

  def withSchemaVersion(version: JSONSchemaVersion): ShapeRenderOptions = copy(schema = version)

  def isWithDocumentation: Boolean                             = documentation
  def isWithCompactedEmission: Boolean                         = compactedEmission
  def shouldEmitWarningForUnsupportedValidationFacets: Boolean = emitWarningForUnsupportedValidationFacets
  def schemaVersion: JSONSchemaVersion                         = schema
}
