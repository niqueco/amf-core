package amf.core.client.platform.model.domain.federation

import amf.core.client.scala.model.domain.federation.{ShapeFederationMetadata => InternalShapeFederationMetadata}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
case class ShapeFederationMetadata(override private[amf] val _internal: InternalShapeFederationMetadata) extends FederationMetadata {

  @JSExportTopLevel("ShapeFederationMetadata")
  def this() = this(InternalShapeFederationMetadata())
}

