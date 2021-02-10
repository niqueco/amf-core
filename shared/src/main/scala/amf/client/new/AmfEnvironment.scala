package amf.client.`new`

import java.util.EventListener

import amf.ProfileName
import amf.client.`new`.amfcore.{AmfLogger, AmfParsePlugin, AmfResolvePlugin, AmfValidatePlugin, MutedLogger}
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
import ExecutionContext.Implicits.global
// all constructors only visible from amf. Users should always use builders or defaults

abstract private[amf] class BaseEnvironment(val resolvers: AmfResolvers,
                                            val errorHandlerProvider: ErrorHandlerProvider,
                                            val registry: AmfRegistry,
                                            val amfConfig: AmfConfig,
                                            val options: AmfOptions){
  private var initialized:Boolean = false

  def init(): Future[Unit] = if(initialized) Future.unit else registry.init().map(_ => initialized = true)

  def getInstance():AmfClient = new AmfClient(this)

  def withResourceLoader(rl:ResourceLoader): this.type = doCopy(resolvers.withResourceLoader(rl))

  def overrideResourceLoaders(rl:Seq[ResourceLoader]): this.type = doCopy(AmfResolvers(rl, resolvers.unitCache))

  def withPlugin(amfPlugin:AmfParsePlugin): this.type = doCopy(registry = registry.withPlugin(amfPlugin))

  private [amf] def beforeParse() = init()

  protected def doCopy(registry: AmfRegistry): this.type

  protected def doCopy(resolvers: AmfResolvers): this.type
}

case class AmfEnvironment(override val resolvers: AmfResolvers,
                          override val errorHandlerProvider: ErrorHandlerProvider,
                          override val registry: AmfRegistry,
                          override val amfConfig: AmfConfig,
                          override val options: AmfOptions) extends BaseEnvironment(resolvers, errorHandlerProvider, registry, amfConfig, options) { // break platform into more specific classes?
  override protected def doCopy(registry: AmfRegistry): AmfEnvironment.this.type = copy(resolvers)

  override protected def doCopy(resolvers: AmfResolvers): AmfEnvironment.this.type = copy(resolvers)
}

object AmfEnvironment{

  def default() = {
    val default = AmfConfig.default
    new AmfEnvironment(
      new AmfResolvers(default.platform.loaders()(default.executionContext.context),None),
      DefaultErrorHandlerProvider,
      AmfRegistry.forEntities,
      default,
      AmfOptions.default
    )
  }

  def fromFile(file:String) = {

  }
}

case class AmfOptions(parsingOptions: ParsingOptions, renderingOptions:RenderOptions, private[amf] var env:AmfEnvironment){
  def withPrettyPrint(): AmfEnvironment = {
    val copied = copy(renderingOptions = renderingOptions.withPrettyPrint)
    val newEnv = env.copy(options = copied)
    copied.env = newEnv
    newEnv
  }
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
    copy(resourceLoader = resourceLoader +: resourceLoaders)
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

