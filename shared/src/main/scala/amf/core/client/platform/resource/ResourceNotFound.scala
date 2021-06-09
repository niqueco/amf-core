package amf.core.client.platform.resource

import amf.core.internal.remote.FileLoaderException

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("ResourceNotFound")
class ResourceNotFound(val msj: String) extends FileLoaderException(msj, new Throwable(msj))
