package amf.core.client.scala.config

import amf.core.client.common.remote.Content
import amf.core.client.platform.config.AMFEventNames._
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.parse.AMFParsePlugin
import amf.core.client.scala.parse.document.ParsedDocument
import amf.core.client.scala.transform.{TransformationPipeline, TransformationStep}
import amf.core.client.scala.validation.AMFValidationReport
import amf.core.internal.plugins.render.AMFRenderPlugin
import amf.core.internal.plugins.validation.AMFValidatePlugin

// interface is duplicated in exported package to maintain separate hierarchy of AMFEvents

/**
  * Defines an event listener linked to a specific [[AMFEvent]]
  */
trait AMFEventListener {
  def notifyEvent(event: AMFEvent)
}

trait AMFEvent {
  val name: String
}

protected[amf] trait GroupedEvent { event: AMFEvent =>
  val groupKey: String
}

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Parsing Events ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

/**
  * Every client invocation to the parsing logic
  * @param url URL of the top level document being parsed
  */
case class StartingParsingEvent(url: String) extends AMFEvent with GroupedEvent {
  override val name: String     = StartedParse
  override val groupKey: String = url
}

/**
  * Called before parsing syntax of certain content.
  * @param url URL of the document being parsed
  * @param content original content that was parsed
  */
case class StartingContentParsingEvent(url: String, content: Content) extends AMFEvent with GroupedEvent {
  override val name: String     = StartedContentParse
  override val groupKey: String = url
}

/**
  * Every successful syntax AST being parsed for any document
  * @param url URL of the document being parsed
  * @param content original content that was parsed
  * @param parsedAST Parsed document AST
  */
case class ParsedSyntaxEvent(url: String, content: Content, parsedAST: ParsedDocument)
    extends AMFEvent
    with GroupedEvent {
  override val name: String     = ParsedSyntax
  override val groupKey: String = url
}

/**
  * Every successful domain model being parsed for any document
  * @param url URL of the document being parsed
  * @param unit Parsed domain unit
  */
case class ParsedModelEvent(url: String, unit: BaseUnit) extends AMFEvent with GroupedEvent {
  override val name: String     = ParsedModel
  override val groupKey: String = url
}

/**
  * Notifies when an [[AMFParsePlugin]] has been selected
  * @param rootLocation location of the document being parsed
  * @param plugin selected [[AMFParsePlugin]]
  */
case class SelectedParsePluginEvent(rootLocation: String, plugin: AMFParsePlugin) extends AMFEvent with GroupedEvent {
  override val name: String     = SelectedParsePlugin
  override val groupKey: String = rootLocation
}

/**
  * Notifies when references have been found while parsing
  * @param rootLocation location of the document being parsed
  * @param amount amount of references found
  */
case class FoundReferencesEvent(rootLocation: String, amount: Int) extends AMFEvent with GroupedEvent {
  override val name: String     = FoundReferences
  override val groupKey: String = rootLocation
}

/**
  * Notifies when a syntax mediatype has been automatically detected
  * @param location location of the document being parsed
  * @param mediaType mediatype detected
  */
case class DetectedSyntaxMediaTypeEvent(location: String, mediaType: String) extends AMFEvent with GroupedEvent {
  override val name: String     = DetectedSyntaxMediaType
  override val groupKey: String = location
}

/**
  * Every successful parser invocation containing the top level domain unit being parsed
  * @param url URL of the top level document being parsed
  * @param unit parsed domain unit for the top level document
  */
case class FinishedParsingEvent(url: String, unit: BaseUnit) extends AMFEvent with GroupedEvent {
  override val name: String     = FinishedParse
  override val groupKey: String = url
}

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Transformation Events ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

/**
  * Notifies when a [[TransformationPipeline]] starts
  * @param pipeline [[TransformationPipeline]] that's starting
  */
case class StartingTransformationEvent(pipeline: TransformationPipeline) extends AMFEvent {
  override val name: String = StartedTransformation
}

/**
  * Notifies when a [[TransformationStep]] starts
  * @param step [[TransformationStep]] that's starting
  * @param index index of the step in the pipeline
  */
case class StartedTransformationStepEvent(step: TransformationStep, index: Int) extends AMFEvent with GroupedEvent {
  override val name: String     = StartedTransformationStep
  override val groupKey: String = s"transform/step-$index"
}

/**
  * Notifies when a [[TransformationStep]] finishes
  * @param step [[TransformationStep]] that finished
  * @param index index of the step in the pipeline
  */
case class FinishedTransformationStepEvent(step: TransformationStep, index: Int) extends AMFEvent with GroupedEvent {
  override val name: String     = FinishedTransformationStep
  override val groupKey: String = s"transform/step-$index"
}

/**
  * Notifies when a [[TransformationPipeline]] ends
  * @param unit the transformed model
  */
case class FinishedTransformationEvent(unit: BaseUnit) extends AMFEvent {
  override val name: String = FinishedTransformation
}

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Validation Events ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

/**
  * Notifies when validation starts
  * @param totalPlugins
  */
case class StartingValidationEvent(totalPlugins: Int) extends AMFEvent {
  override val name: String = StartingValidation
}

/**
  * Notifies Every time an [[AMFValidatePlugin]] finishes
  * @param plugin the [[AMFValidatePlugin]] that finished
  * @param result the resulting [[AMFValidationReport]]
  */
case class FinishedValidationPluginEvent(plugin: AMFValidatePlugin, result: AMFValidationReport) extends AMFEvent {
  override val name: String = FinishedValidationPlugin
}

/**
  * Notifies when validation ends
  * @param result the resulting [[AMFValidationReport]]
  */
case class FinishedValidationEvent(result: AMFValidationReport) extends AMFEvent {
  override val name: String = FinishedValidation
}

// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Render Events ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

/**
  * Notifies when starting to render to writer
  * @param unit [[BaseUnit]] to render
  * @param mediaType optional mediatype to render to
  */
case class StartingRenderToWriterEvent(unit: BaseUnit, mediaType: Option[String]) extends AMFEvent with GroupedEvent {
  override val name: String     = StartedRenderToWriter
  override val groupKey: String = unit.id
}

/**
  * Notifies when rendering starts
  * @param unit [[BaseUnit]] to render
  * @param plugin [[AMFRenderPlugin]] being used
  * @param mediaType optional mediatype to render to
  */
case class StartingRenderingEvent(unit: BaseUnit, plugin: AMFRenderPlugin, mediaType: Option[String])
    extends AMFEvent
    with GroupedEvent {
  override val name: String     = StartedRender
  override val groupKey: String = unit.id
}

/**
  * Notifies when Every time an AST finishes rendering
  * @param unit [[BaseUnit]] that was rendered
  * @param renderedAST resultant document
  */
case class FinishedRenderingASTEvent(unit: BaseUnit, renderedAST: ParsedDocument) extends AMFEvent with GroupedEvent {
  override val name: String     = FinishedASTRender
  override val groupKey: String = unit.id
}

/**
  * Notifies when an [[amf.core.client.scala.render.AMFSyntaxRenderPlugin]] finishes rendering
  * @param unit [[BaseUnit]] rendered
  */
case class FinishedRenderingSyntaxEvent(unit: BaseUnit) extends AMFEvent with GroupedEvent {
  override val name: String     = FinishedSyntaxRender
  override val groupKey: String = unit.id
}

case class SkippedValidationPluginEvent(pluginName: String, reason: String) extends AMFEvent {
  override val name: String = SkippedValidationPlugin
}
