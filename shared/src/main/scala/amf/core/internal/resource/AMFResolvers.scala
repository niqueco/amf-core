package amf.core.internal.resource

import amf.core.client.platform.execution.BaseExecutionEnvironment
import amf.core.client.common.remote.Content
import amf.core.client.scala.config.UnitCache
import amf.core.internal.remote.UnsupportedUrlScheme
import amf.core.internal.unsafe.PlatformSecrets

import scala.concurrent.{ExecutionContext, Future}

/**
  * Configuration object for resolvers
  *
  * @param resourceLoaders  a list of [[amf.core.internal.resource.ResourceLoader]] to use
  * @param unitCache        a [[UnitCache]] that stores [[amf.core.client.scala.model.document.BaseUnit]] resolved
  * @param executionContext the [[amf.core.client.platform.execution.BaseExecutionEnvironment]] to use
  */
private[amf] case class AMFResolvers(resourceLoaders: List[ResourceLoader],
                                     val unitCache: Option[UnitCache],
                                     val executionContext: BaseExecutionEnvironment) {

  /**
    *
    * @param resourceLoader
    * @return
    */
  def withResourceLoader(resourceLoader: ResourceLoader): AMFResolvers = {
    copy(resourceLoaders = resourceLoader +: resourceLoaders)
  }

  /**
    *
    * @param newLoaders
    * @return
    */
  def withResourceLoaders(newLoaders: List[ResourceLoader]): AMFResolvers = {
    copy(resourceLoaders = newLoaders)
  }

  /**
    *
    * @param cache
    * @return
    */
  def withCache(cache: UnitCache): AMFResolvers = {
    copy(unitCache = Some(cache))
  }

  /**
    *
    * @param url
    * @param executionContext
    * @return
    */
  def resolveContent(url: String): Future[Content] = {
    loaderConcat(url, resourceLoaders.filter(_.accepts(url)))(executionContext.executionContext)
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

object AMFResolvers extends PlatformSecrets {

  /**
    * Predefined amf resolvers with:
    *  - Default resource loaders by platform
    *  - Without units cache
    * @return
    */
  def predefined() = {
    // TODO: user execution environment?
    AMFResolvers(platform.loaders()((platform.defaultExecutionEnvironment).executionContext).toList,
                 None,
                 (platform.defaultExecutionEnvironment))
  }
}
