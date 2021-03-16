package amf.client.remod.amfcore.config

import amf.client.remote.Content
import amf.core.remote.UnsupportedUrlScheme
import amf.internal.reference.UnitCache
import amf.internal.resource.ResourceLoader

import scala.concurrent.{ExecutionContext, Future}

private[remod] case class AMFResolvers(resourceLoaders: Seq[ResourceLoader], val unitCache: Option[UnitCache]) {


  def withResourceLoader(resourceLoader: ResourceLoader): AMFResolvers = {
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
