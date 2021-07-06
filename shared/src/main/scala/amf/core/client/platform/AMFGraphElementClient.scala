package amf.core.client.platform

import amf.core.client.scala.{AMFGraphElementClient => InternalAMFElementClient}
import amf.core.internal.convert.CoreClientConverters._

import scala.scalajs.js.annotation.JSExportAll

/**
  * Contains common AMF graph operations not associated to documents.
  * Base client for <code>AMLElementClient</code> and <code>AMLElementClient</code>.
  */
@JSExportAll
class AMFGraphElementClient private[amf] (private val _internal: InternalAMFElementClient) {

  private[amf] def this(configuration: AMFGraphConfiguration) = {
    this(new InternalAMFElementClient(configuration))
  }

  def getConfiguration(): AMFGraphConfiguration = _internal.getConfiguration

}
