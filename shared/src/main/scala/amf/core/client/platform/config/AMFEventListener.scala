package amf.core.client.platform.config

import amf.core.client.common.remote.Content
import amf.core.client.platform.model.document.BaseUnit
import amf.core.client.platform.transform.{TransformationPipeline, TransformationStep}
import amf.core.client.platform.validation.AMFValidationReport
import amf.core.client.scala.config
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.internal.convert.CoreClientConverters._
import amf.core.internal.convert.TransformationPipelineConverter._

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** Defines an event listener linked to a specific [[AMFEvent]]
  */
@JSExportAll
trait AMFEventListener {
  def notifyEvent(event: AMFEvent)
}

@JSExportAll
sealed trait AMFEvent {
  val name: String
}

@JSExportTopLevel("EventNames")
@JSExportAll
object AMFEventNames {
  val StartedParse               = "StartedParse"
  val StartedContentParse        = "StartedContentParse"
  val ParsedSyntax               = "ParsedSyntax"
  val ParsedModel                = "ParsedModel"
  val FinishedParse              = "FinishedParse"
  val StartedTransformation      = "StartedTransformation"
  val FinishedTransformationStep = "FinishedTransformationStep"
  val StartedTransformationStep  = "StartedTransformationStep"
  val FinishedTransformation     = "FinishedTransformation"
  val StartingValidation         = "StartingValidation"
  val FinishedValidationPlugin   = "FinishedValidationPlugin"
  val FinishedValidation         = "FinishedValidation"
  val StartedRender              = "StartedRender"
  val StartedRenderToWriter      = "StartedRenderToWriter"
  val FinishedASTRender          = "FinishedASTRender"
  val FinishedSyntaxRender       = "FinishedSyntaxRender"
  val FoundReferences            = "FoundReferences"
  val SelectedParsePlugin        = "SelectedParsePlugin"
  val DetectedSyntaxMediaType    = "DetectedSyntaxMediaType"
  val SkippedValidationPlugin    = "SkippedValidationPlugin"
  val UnitCacheHit               = "UnitCacheHit"
}

object AMFEventConverter {
  def asClient(e: config.AMFEvent): AMFEvent = e match {
    case e: config.StartingParsingEvent            => new StartingParsingEvent(e)
    case e: config.StartingContentParsingEvent     => new StartingContentParsingEvent(e)
    case e: config.ParsedSyntaxEvent               => new ParsedSyntaxEvent(e)
    case e: config.ParsedModelEvent                => new ParsedModelEvent(e)
    case e: config.FinishedParsingEvent            => new FinishedParsingEvent(e)
    case e: config.StartingTransformationEvent     => new StartingTransformationEvent(e)
    case e: config.FinishedTransformationStepEvent => new FinishedTransformationStepEvent(e)
    case e: config.FinishedTransformationEvent     => new FinishedTransformationEvent(e)
    case e: config.StartingValidationEvent         => new StartingValidationEvent(e)
    case e: config.FinishedValidationPluginEvent   => new FinishedValidationPluginEvent(e)
    case e: config.FinishedValidationEvent         => new FinishedValidationEvent(e)
    case e: config.StartingRenderingEvent          => new StartingRenderingEvent(e)
    case e: config.FinishedRenderingASTEvent       => new FinishedRenderingASTEvent(e)
    case e: config.FinishedRenderingSyntaxEvent    => new FinishedRenderingSyntaxEvent(e)
    case e: config.DetectedSyntaxMediaTypeEvent    => DetectedSyntaxMediaTypeEvent(e)
    case e: config.FoundReferencesEvent            => FoundReferencesEvent(e)
    case e: config.SelectedParsePluginEvent        => SelectedParsePluginEvent(e)
    case e: config.StartedTransformationStepEvent  => StartedTransformationStepEvent(e)
    case e: config.StartingRenderToWriterEvent     => StartingRenderToWriterEvent(e)
    case e: config.SkippedValidationPluginEvent    => SkippedValidationPluginEvent(e)
    case e: config.UnitCacheHitEvent               => UnitCacheHitEvent(e)
  }
}

abstract class ClientEvent(private val _internal: config.AMFEvent) extends AMFEvent {
  override val name: String = _internal.name
}

// Parsing Events

/** Every client invocation to the parsing logic
  */
@JSExportAll
class StartingParsingEvent(private val _internal: config.StartingParsingEvent) extends ClientEvent(_internal) {
  def url: String = _internal.url
}

/** Called before parsing syntax of certain content.
  */
@JSExportAll
class StartingContentParsingEvent(private val _internal: config.StartingContentParsingEvent)
    extends ClientEvent(_internal) {
  def url: String      = _internal.url
  def content: Content = _internal.content
}

/** Every successful syntax AST being parsed for any document
  */
@JSExportAll
class ParsedSyntaxEvent(private val _internal: config.ParsedSyntaxEvent) extends ClientEvent(_internal) {
  def url: String      = _internal.url
  def content: Content = _internal.content
}

/** Every successful domain model being parsed for any document
  */
@JSExportAll
class ParsedModelEvent(private val _internal: config.ParsedModelEvent) extends ClientEvent(_internal) {
  def url: String    = _internal.url
  def unit: BaseUnit = _internal.unit
}

/** Every successful parser invocation containing the top level domain unit being parsed
  */
@JSExportAll
class FinishedParsingEvent(private val _internal: config.FinishedParsingEvent) extends ClientEvent(_internal) {
  def url: String    = _internal.url
  def unit: BaseUnit = _internal.unit
}

// Resolution Events

/** Every client invocation to the transformation logic
  */
@JSExportAll
class StartingTransformationEvent(private val _internal: config.StartingTransformationEvent)
    extends ClientEvent(_internal) {
  def pipeline: TransformationPipeline = _internal.pipeline
}

/** Notifies every time a transformation step finishes
  */
@JSExportAll
class FinishedTransformationStepEvent(private val _internal: config.FinishedTransformationStepEvent)
    extends ClientEvent(_internal) {
  def step: TransformationStep = _internal.step
  def index: Int               = _internal.index
}

/** Notifies every time a transformation pipeline finishes
  */
@JSExportAll
class FinishedTransformationEvent(private val _internal: config.FinishedTransformationEvent)
    extends ClientEvent(_internal) {
  def unit: BaseUnit = _internal.unit
}

// Validation Events

/** Notifies when validation starts
  */
@JSExportAll
class StartingValidationEvent(private val _internal: config.StartingValidationEvent) extends ClientEvent(_internal) {
  def totalPlugins: Int = _internal.totalPlugins
}

/** Notifies every time a validation plugin finishes
  */
@JSExportAll
class FinishedValidationPluginEvent(private val _internal: config.FinishedValidationPluginEvent)
    extends ClientEvent(_internal) {
  def result: AMFValidationReport = _internal.result
}

/** Notifies every time validation logic finishes
  */
@JSExportAll
class FinishedValidationEvent(private val _internal: config.FinishedValidationEvent) extends ClientEvent(_internal) {
  def result: AMFValidationReport = _internal.result
}

// Rendering Events

/** Notifies when rendering starts
  */
@JSExportAll
class StartingRenderingEvent(private val _internal: config.StartingRenderingEvent) extends ClientEvent(_internal) {
  def unit: BaseUnit                  = _internal.unit
  def mediaType: ClientOption[String] = _internal.mediaType.asClient
}

/** Notifies when starting to render to writer
  */
@JSExportAll
case class StartingRenderToWriterEvent(private val _internal: config.StartingRenderToWriterEvent)
    extends ClientEvent(_internal) {}

/** Notifies when an AST finishes rendering
  */
@JSExportAll
class FinishedRenderingASTEvent(private val _internal: config.FinishedRenderingASTEvent)
    extends ClientEvent(_internal) {
  def unit: BaseUnit = _internal.unit
}

/** Notifies when a syntax finishes rendering
  */
@JSExportAll
class FinishedRenderingSyntaxEvent(private val _internal: config.FinishedRenderingSyntaxEvent)
    extends ClientEvent(_internal) {
  def unit: BaseUnit = _internal.unit
}

/** Notifies when a parse plugin has been selected
  */
@JSExportAll
case class SelectedParsePluginEvent(private val _internal: config.SelectedParsePluginEvent)
    extends ClientEvent(_internal) {}

/** Notifies when references are found in parsing
  */
@JSExportAll
case class FoundReferencesEvent(private val _internal: config.FoundReferencesEvent) extends ClientEvent(_internal) {
  def root: String = _internal.rootLocation
  def amount: Int  = _internal.references.length
}

/** Notifies when a mediatype has been auto detected
  */
@JSExportAll
case class DetectedSyntaxMediaTypeEvent(private val _internal: config.DetectedSyntaxMediaTypeEvent)
    extends ClientEvent(_internal) {}

/** Notifies when a transformation step starts
  */
@JSExportAll
case class StartedTransformationStepEvent(private val _internal: config.StartedTransformationStepEvent)
    extends ClientEvent(_internal) {}

@JSExportAll
case class SkippedValidationPluginEvent(private val _internal: config.SkippedValidationPluginEvent)
    extends ClientEvent(_internal) {}

@JSExportAll
case class UnitCacheHitEvent(private val _internal: config.UnitCacheHitEvent) extends ClientEvent(_internal) {}
