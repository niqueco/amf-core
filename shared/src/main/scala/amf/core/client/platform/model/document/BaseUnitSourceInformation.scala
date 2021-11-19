package amf.core.client.platform.model.document

import amf.core.client.platform.model.{AmfObjectWrapper, BoolField, StrField}
import amf.core.client.scala.model.document.{BaseUnitSourceInformation => InternalBaseUnitSourceInformation}
import amf.core.client.scala.model.document.{LocationInformation => InternalLocationInformation}
import amf.core.internal.convert.CoreClientConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
class BaseUnitSourceInformation(private[amf] val _internal: InternalBaseUnitSourceInformation)
    extends AmfObjectWrapper {

  @JSExportTopLevel("BaseUnitSourceInformation")
  def this() = this(InternalBaseUnitSourceInformation())

  def rootLocation: StrField = _internal.rootLocation

  def withRootLocation(value: String): this.type = {
    _internal.withRootLocation(value)
    this
  }

  def additionalLocations: ClientList[LocationInformation] = _internal.additionalLocations.asClient

  def withAdditionalLocations(locations: ClientList[LocationInformation]): this.type = {
    _internal.withAdditionalLocations(locations.asInternal)
    this
  }
}

@JSExportAll
class LocationInformation(private[amf] val _internal: InternalLocationInformation) extends AmfObjectWrapper {

  @JSExportTopLevel("LocationInformation")
  def this() = this(InternalLocationInformation())

  def locationValue: StrField = _internal.locationValue

  def withLocation(value: String): this.type = {
    _internal.withLocation(value)
    this
  }

  def elements(): ClientList[StrField] = _internal.elements.asClient

  def withElements(elements: ClientList[String]): this.type = {
    _internal.withElements(elements.asInternal)
    this
  }

}
