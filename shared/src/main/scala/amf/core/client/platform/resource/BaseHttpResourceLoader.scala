package amf.core.client.platform.resource

import amf.core.internal.remote.HttpParts

import scala.scalajs.js.annotation.JSExportAll

@JSExportAll
abstract class BaseHttpResourceLoader extends ResourceLoader {

  override def accepts(resource: String): Boolean = resource match {
    case HttpParts(_, _, _) => true
    case _                  => false
  }
}
