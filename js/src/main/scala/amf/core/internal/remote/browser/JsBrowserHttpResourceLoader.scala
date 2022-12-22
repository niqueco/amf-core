package amf.core.internal.remote.browser

import amf.core.client.common.remote.Content
import amf.core.client.platform.resource.BaseHttpResourceLoader
import amf.core.internal.remote.{NetworkError, UnexpectedStatusCode}
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichFutureNonThenable
import scala.scalajs.js.Thenable.Implicits.thenable2future
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportTopLevel("JsBrowserHttpResourceLoader")
@JSExportAll
case class JsBrowserHttpResourceLoader() extends BaseHttpResourceLoader {

  override def fetch(resource: String): js.Promise[Content] = {
    dom
      .fetch(resource)
      .flatMap(xhr =>
        xhr.status match {
          case 200 => xhr.text().map(text => new Content(text, resource))
          case s   => Future.failed(UnexpectedStatusCode(resource, s))
        }
      )
      .recover { case e: Throwable =>
        throw NetworkError(e)
      }
      .toJSPromise
  }
}
