package amf.client.`new`

import java.util.EventListener
import amf.ProfileName
import amf.client.`new`.amfcore.{AMFLogger, AMFParsePlugin, AMFPlugin, AMFValidatePlugin, MutedLogger}
import amf.client.execution.BaseExecutionEnvironment
import amf.client.remote.Content
import amf.core.client.ParsingOptions
import amf.core.emitter.RenderOptions
import amf.core.errorhandling.ErrorHandler
import amf.core.execution.ExecutionEnvironment
import amf.core.model.document.BaseUnit
import amf.core.remote.{Aml, Platform, UnsupportedUrlScheme, Vendor}
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.reference.UnitCache
import amf.internal.resource.ResourceLoader
import org.mulesoft.common.io.FileSystem
import org.yaml.model.YDocument

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
// all constructors only visible from amf. Users should always use builders or defaults

abstract private[amf] class BaseEnvironment(val resolvers: AMFResolvers,
                                            val errorHandlerProvider: ErrorHandlerProvider,
                                            val registry: AMFRegistry,
                                            val amfConfig: AMFConfig,
                                            val options: AMFOptions){
  type Self <: BaseEnvironment
//  private var initialized:Boolean = false

//  def init(): Future[Unit] = if(initialized) Future.unit else registry.init().map(_ => initialized = true)

  def getInstance():AMFClient = new AMFClient(this)

  def withResourceLoader(rl:ResourceLoader): Self = doCopy(resolvers.withResourceLoader(rl))

  def overrideResourceLoaders(rl:Seq[ResourceLoader]): Self = doCopy(AMFResolvers(rl, resolvers.unitCache))

  def withUnitCache(cache: UnitCache): Self = doCopy(AMFResolvers(resolvers.resourceLoaders, Some(cache)))

  def withPlugin(amfPlugin: AMFParsePlugin): Self = doCopy(registry.withPlugin(amfPlugin))

  def withPlugins(plugins: List[AMFPlugin[_]]): Self = doCopy(registry.withPlugins(plugins))

//  private [amf] def beforeParse() = init()

  protected def doCopy(registry: AMFRegistry): Self

  protected def doCopy(resolvers: AMFResolvers): Self
}

object BaseEnvironment {
  def fromLegacy(base: BaseEnvironment, legacy: Environment): BaseEnvironment = {
    legacy.maxYamlReferences.foreach{ maxValue => base.options.parsingOptions.setMaxYamlReferences(maxValue) }
    val withLoaders = base.overrideResourceLoaders(legacy.loaders)
    legacy.resolver.map( unitCache => withLoaders.withUnitCache(unitCache)).getOrElse(withLoaders)
  }
}

case class AMFEnvironment(override val resolvers: AMFResolvers,
                          override val errorHandlerProvider: ErrorHandlerProvider,
                          override val registry: AMFRegistry,
                          override val amfConfig: AMFConfig,
                          override val options: AMFOptions) extends BaseEnvironment(resolvers, errorHandlerProvider, registry, amfConfig, options) { // break platform into more specific classes?
  type Self = AMFEnvironment

  def withParsingOptions(parsingOptions: ParsingOptions): AMFEnvironment = this.copy(options = options.copy(parsingOptions = parsingOptions))

  override protected def doCopy(registry: AMFRegistry): Self = this.copy(registry = registry)

  override protected def doCopy(resolvers: AMFResolvers): Self = this.copy(resolvers = resolvers)

  /**
    * Merges two environments taking into account specific attributes that can be merged.
    * This is currently limited to: registry plugins.
    */
  def merge(other: AMFEnvironment): AMFEnvironment = {
    this.withPlugins(other.registry.plugins.allPlugins)
  }

}

object AMFEnvironment {

  def default(): AMFEnvironment = {
    val config = AMFConfig.default()
    new AMFEnvironment(
      AMFResolvers(config.platform.loaders()(config.executionContext.context),None),
      DefaultErrorHandlerProvider,
      AMFRegistry.empty,
      config,
      AMFOptions.default()
    )
  }

}

// TODO both options here are mutable and must be replaced
case class AMFOptions(parsingOptions: ParsingOptions, renderingOptions:RenderOptions /*, private[amf] var env:AmfEnvironment*/){
//  def withPrettyPrint(): AmfEnvironment = {
//    val copied = copy(renderingOptions = renderingOptions.withPrettyPrint)
//    val newEnv = env.copy(options = copied)
//    copied.env = newEnv
//    newEnv
//  }
}

object AMFOptions {
  def default() = new AMFOptions(ParsingOptions(), RenderOptions())
}

class AMFConfig(private val logger: AMFLogger,
                private val listeners: List[EventListener],
                val platform: Platform,
                val executionContext: ExecutionEnvironment,
                private val idGenerator: AMFIdGenerator)

object AMFConfig extends PlatformSecrets{
  def default() = new AMFConfig(MutedLogger, Nil,platform, ExecutionEnvironment(), PathAMFIdGenerator$)
}
// environment class
case class AMFResolvers(val resourceLoaders: Seq[ResourceLoader], val unitCache: Option[UnitCache]) {


  def withResourceLoader(resourceLoader: ResourceLoader) = {
    copy(resourceLoaders = resourceLoader +: resourceLoaders)
  }

  def resolveContent(url: String)(implicit executionContext: ExecutionContext): Future[Content] = {
    loaderConcat(url, resourceLoaders.filter(_.accepts(url)))
  }

  private def loaderConcat(url: String, loaders: Seq[ResourceLoader])(
      implicit executionContext: ExecutionContext): Future[Content] = loaders.toList match {
    case Nil         => throw new UnsupportedUrlScheme(url)
    case head :: Nil => head.fetch(url)
    case head :: tail =>
      head.fetch(url).recoverWith {
        case _ => loaderConcat(url, tail)
      }
  }

}

