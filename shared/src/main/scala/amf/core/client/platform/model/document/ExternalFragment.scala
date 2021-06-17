package amf.core.client.platform.model.document

import amf.core.client.scala.model.document.{ExternalFragment => InternalExternalFragment}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
case class ExternalFragment(override private[amf] val _internal: InternalExternalFragment)
    extends Fragment(_internal) {

  @JSExportTopLevel("ExternalFragment")
  def this() = this(InternalExternalFragment())
}
