package amf.client.remod.amfcore.config

import amf.core.errorhandling.{ErrorHandler, UnhandledErrorHandler}
import amf.core.metamodel.Field
import amf.core.emitter.{RenderOptions => LegacyRenderOptions}
/**
  * Immutable implementation of render options
  */
private[amf] case class RenderOptions(
    compactedEmission: Boolean = true,
    sources: Boolean = false,
    compactUris: Boolean = false,
    rawSourceMaps: Boolean = false,
    validating: Boolean = false,
    filterFields: Field => Boolean = (_: Field) => false,
    amfJsonLdSerialization: Boolean = true,
    useJsonLdEmitter: Boolean = false,
    flattenedJsonLd: Boolean = false,
    eh: ErrorHandler = UnhandledErrorHandler,
    prettyPrint: Boolean = false,
    emitNodeIds: Boolean = false,
) {

  def withCompactedEmission: RenderOptions = copy(compactedEmission = true)

  def withoutCompactedEmission: RenderOptions = copy(compactedEmission = false)

  def withPrettyPrint: RenderOptions = copy(prettyPrint = true)

  def withoutPrettyPrint: RenderOptions = copy(prettyPrint = true)

  /** Include source maps when rendering to graph. */
  def withSourceMaps: RenderOptions = copy(sources = true)

  /** Include source maps when rendering to graph. */
  def withoutSourceMaps: RenderOptions = copy(sources = false)

  def withCompactUris: RenderOptions = copy(compactUris = true)

  def withoutCompactUris: RenderOptions = copy(compactUris = false)

  def withRawSourceMaps: RenderOptions = copy(rawSourceMaps = true)

  def withoutRawSourceMaps: RenderOptions = copy(rawSourceMaps = false)

  def withValidation: RenderOptions = copy(validating = true)

  def withNodeIds: RenderOptions = copy(emitNodeIds = true)

  def withoutNodeIds: RenderOptions = copy(emitNodeIds = false)

  def withoutValidation: RenderOptions = copy(validating = false)

  def withFilterFieldsFunc(f: Field => Boolean): RenderOptions = copy(filterFields = f)

  def withoutAmfJsonLdSerialization: RenderOptions = copy(amfJsonLdSerialization = false)

  def withAmfJsonLdSerialization: RenderOptions = copy(amfJsonLdSerialization = true)

  def withErrorHandler(errorHandler: ErrorHandler): RenderOptions = copy(eh = errorHandler)

  def withFlattenedJsonLd: RenderOptions = copy(flattenedJsonLd = true)

  def withoutFlattenedJsonLd: RenderOptions = copy(flattenedJsonLd = false)

  def isFlattenedJsonLd: Boolean = flattenedJsonLd

  def isWithCompactedEmission: Boolean   = compactedEmission
  def isCompactUris: Boolean             = compactUris
  def isWithSourceMaps: Boolean          = sources
  def isWithRawSourceMaps: Boolean       = rawSourceMaps
  def isAmfJsonLdSerilization: Boolean   = amfJsonLdSerialization
  def isValidation: Boolean              = validating
  def renderField(field: Field): Boolean = !filterFields(field)
  def errorHandler: ErrorHandler         = eh
  def isPrettyPrint: Boolean             = prettyPrint
  def isEmitNodeIds: Boolean             = emitNodeIds
}
