package amf.client.`new`

import java.util.EventListener

import amf.ProfileName
import amf.client.`new`.amfcore.{AmfLogger, AmfParsePlugin, AmfValidatePlugin, MutedLogger}
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

abstract private[amf] class BaseEnvironment(val resolvers: AmfResolvers,
                                            val errorHandlerProvider: ErrorHandlerProvider,
                                            val registry: AmfRegistry,
                                            val amfConfig: AmfConfig,
                                            val options: AmfOptions){
  type Self <: BaseEnvironment
//  private var initialized:Boolean = false

//  def init(): Future[Unit] = if(initialized) Future.unit else registry.init().map(_ => initialized = true)

  def getInstance():AmfClient = new AmfClient(this)

  def withResourceLoader(rl:ResourceLoader): Self = doCopy(resolvers.withResourceLoader(rl))

  def overrideResourceLoaders(rl:Seq[ResourceLoader]): Self = doCopy(AmfResolvers(rl, resolvers.unitCache))

  def withUnitCache(cache: UnitCache): Self = doCopy(AmfResolvers(resolvers.resourceLoaders, Some(cache)))

  def withPlugin(amfPlugin: AmfParsePlugin): Self = doCopy(registry.withPlugin(amfPlugin))

//  private [amf] def beforeParse() = init()

  protected def doCopy(registry: AmfRegistry): Self

  protected def doCopy(resolvers: AmfResolvers): Self
}

object BaseEnvironment {
  def fromLegacy(base: BaseEnvironment, legacy: Environment): BaseEnvironment = {
    legacy.maxYamlReferences.foreach{ maxValue => base.options.parsingOptions.setMaxYamlReferences(maxValue) }
    val withLoaders = base.overrideResourceLoaders(legacy.loaders)
    legacy.resolver.map( unitCache => withLoaders.withUnitCache(unitCache)).getOrElse(withLoaders)
  }
}

case class AmfEnvironment(override val resolvers: AmfResolvers,
                          override val errorHandlerProvider: ErrorHandlerProvider,
                          override val registry: AmfRegistry,
                          override val amfConfig: AmfConfig,
                          override val options: AmfOptions) extends BaseEnvironment(resolvers, errorHandlerProvider, registry, amfConfig, options) { // break platform into more specific classes?
  type Self = AmfEnvironment

  def withParsingOptions(parsingOptions: ParsingOptions): AmfEnvironment = this.copy(options = options.copy(parsingOptions = parsingOptions))

  override protected def doCopy(registry: AmfRegistry): Self = this.copy(registry = registry)

  override protected def doCopy(resolvers: AmfResolvers): Self = this.copy(resolvers = resolvers)

}

object AmfEnvironment {

  def default() = {
    val config = AmfConfig.default
    new AmfEnvironment(
      AmfResolvers(config.platform.loaders()(config.executionContext.context),None),
      DefaultErrorHandlerProvider,
      AmfRegistry.empty,
      config,
      AmfOptions.default
    )
  }

  def fromFile(file:String) = {

  }
}

case class AmfOptions(parsingOptions: ParsingOptions, renderingOptions:RenderOptions/*, private[amf] var env:AmfEnvironment*/){
//  def withPrettyPrint(): AmfEnvironment = {
//    val copied = copy(renderingOptions = renderingOptions.withPrettyPrint)
//    val newEnv = env.copy(options = copied)
//    copied.env = newEnv
//    newEnv
//  }
}

object AmfOptions{
  val default = new AmfOptions(ParsingOptions(), RenderOptions())
}
class AmfConfig(private val logger: AmfLogger,
                private val listeners: List[EventListener],
                val platform: Platform,
                val executionContext: ExecutionEnvironment,
                private val idGenerator: AmfIdGenerator)

object AmfConfig extends PlatformSecrets{
  def default = new AmfConfig(MutedLogger, Nil,platform, ExecutionEnvironment(),PathAmfIdGenerator)
}
// environment class
case class AmfResolvers(val resourceLoaders: Seq[ResourceLoader], val unitCache: Option[UnitCache]) {


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

