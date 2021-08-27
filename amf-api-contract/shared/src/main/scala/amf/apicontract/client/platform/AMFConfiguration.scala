package amf.apicontract.client.platform

import amf.aml.client.platform.BaseAMLConfiguration
import amf.aml.client.platform.model.document.Dialect
import amf.aml.internal.convert.VocabulariesClientConverter.DialectConverter
import amf.apicontract.client.scala.{
  APIConfiguration => InternalAPIConfiguration,
  AsyncAPIConfiguration => InternalAsyncAPIConfiguration,
  OASConfiguration => InternalOASConfiguration,
  RAMLConfiguration => InternalRAMLConfiguration,
  WebAPIConfiguration => InternalWebAPIConfiguration
}
import amf.apicontract.internal.convert.ApiClientConverters._
import amf.core.client.platform.config.{AMFEventListener, ParsingOptions, RenderOptions}
import amf.core.client.platform.errorhandling.ErrorHandlerProvider
import amf.core.client.platform.reference.UnitCache
import amf.core.client.platform.resource.ResourceLoader
import amf.core.client.platform.transform.TransformationPipeline
import amf.core.internal.convert.ClientErrorHandlerConverter._
import amf.core.internal.convert.TransformationPipelineConverter._

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import amf.apicontract.client.scala
import amf.core.client.platform.AMFGraphConfiguration
import amf.core.client.platform.execution.BaseExecutionEnvironment
import amf.core.client.platform.validation.payload.AMFShapePayloadValidationPlugin
import amf.core.internal.convert.PayloadValidationPluginConverter.PayloadValidationPluginMatcher
import amf.core.internal.remote.Spec

@JSExportAll
class AMFConfiguration private[amf] (private[amf] override val _internal: scala.AMFConfiguration)
    extends BaseAMLConfiguration(_internal) {

  override def baseUnitClient(): AMFBaseUnitClient = new AMFBaseUnitClient(this)
  override def elementClient(): AMFElementClient   = new AMFElementClient(this)
  def configurationState(): AMFConfigurationState  = new AMFConfigurationState(this)

  override def withParsingOptions(parsingOptions: ParsingOptions): AMFConfiguration =
    _internal.withParsingOptions(parsingOptions)

  override def withResourceLoader(rl: ResourceLoader): AMFConfiguration =
    _internal.withResourceLoader(ResourceLoaderMatcher.asInternal(rl))

  override def withResourceLoaders(rl: ClientList[ResourceLoader]): AMFConfiguration =
    _internal.withResourceLoaders(rl.asInternal.toList)

  override def withUnitCache(cache: UnitCache): AMFConfiguration =
    _internal.withUnitCache(UnitCacheMatcher.asInternal(cache))

  override def withTransformationPipeline(pipeline: TransformationPipeline): AMFConfiguration =
    _internal.withTransformationPipeline(pipeline)

  override def withRenderOptions(renderOptions: RenderOptions): AMFConfiguration =
    _internal.withRenderOptions(renderOptions)

  override def withErrorHandlerProvider(provider: ErrorHandlerProvider): AMFConfiguration =
    _internal.withErrorHandlerProvider(() => provider.errorHandler())

  override def withEventListener(listener: AMFEventListener): AMFConfiguration = _internal.withEventListener(listener)

  override def withExecutionEnvironment(executionEnv: BaseExecutionEnvironment): AMFConfiguration =
    _internal.withExecutionEnvironment(executionEnv._internal)

  override def withDialect(dialect: Dialect): AMFConfiguration = _internal.withDialect(asInternal(dialect))

  def withDialect(path: String): ClientFuture[AMFConfiguration] = _internal.withDialect(path).asClient

  def forInstance(url: String): ClientFuture[AMFConfiguration] = _internal.forInstance(url).asClient

  override def withShapePayloadPlugin(plugin: AMFShapePayloadValidationPlugin): AMFConfiguration =
    _internal.withPlugin(PayloadValidationPluginMatcher.asInternal(plugin))
}

/**
  * common configuration with all configurations needed for RAML like:
  *   - Validation rules
  *   - Parse and emit plugins
  *   - Transformation Pipelines
  */
@JSExportAll
@JSExportTopLevel("RAMLConfiguration")
object RAMLConfiguration {
  def RAML10(): AMFConfiguration             = InternalRAMLConfiguration.RAML10()
  def RAML08(): AMFConfiguration             = InternalRAMLConfiguration.RAML08()
  def RAML(): AMFConfiguration               = InternalRAMLConfiguration.RAML()
  def fromSpec(spec: Spec): AMFConfiguration = InternalRAMLConfiguration.fromSpec(spec)
}

/**
  * common configuration with all configurations needed for OAS like:
  *   - Validation rules
  *   - Parse and emit plugins
  *   - Transformation Pipelines
  */
@JSExportAll
@JSExportTopLevel("OASConfiguration")
object OASConfiguration {
  def OAS20(): AMFConfiguration              = InternalOASConfiguration.OAS20()
  def OAS30(): AMFConfiguration              = InternalOASConfiguration.OAS30()
  def OAS(): AMFConfiguration                = InternalOASConfiguration.OAS()
  def fromSpec(spec: Spec): AMFConfiguration = InternalOASConfiguration.fromSpec(spec)
}

/** Merged [[OASConfiguration]] and [[RAMLConfiguration]] configurations */
@JSExportAll
@JSExportTopLevel("WebAPIConfiguration")
object WebAPIConfiguration {
  def WebAPI(): AMFConfiguration             = InternalWebAPIConfiguration.WebAPI()
  def fromSpec(spec: Spec): AMFConfiguration = InternalWebAPIConfiguration.fromSpec(spec)
}

/**
  * common configuration with all configurations needed for AsyncApi like:
  *   - Validation rules
  *   - Parse and emit plugins
  *   - Transformation Pipelines
  */
@JSExportAll
@JSExportTopLevel("AsyncAPIConfiguration")
object AsyncAPIConfiguration {
  def Async20(): AMFConfiguration = InternalAsyncAPIConfiguration.Async20()
}

@JSExportAll
@JSExportTopLevel("APIConfiguration")
object APIConfiguration {
  def API(): AMFConfiguration                = InternalAPIConfiguration.API()
  def fromSpec(spec: Spec): AMFConfiguration = InternalAPIConfiguration.fromSpec(spec)
}