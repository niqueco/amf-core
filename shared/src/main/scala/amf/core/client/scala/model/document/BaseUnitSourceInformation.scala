package amf.core.client.scala.model.document

import amf.core.client.scala.model.StrField
import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.metamodel.Obj
import amf.core.internal.metamodel.document.{BaseUnitModel, BaseUnitSourceInformationModel, LocationInformationModel}
import amf.core.internal.metamodel.document.BaseUnitSourceInformationModel._
import amf.core.internal.metamodel.document.LocationInformationModel._
import amf.core.internal.parser.domain.{Annotations, Fields}

class BaseUnitSourceInformation(val fields: Fields, val annotations: Annotations) extends AmfObject {

  /** Value , path + field value that is used to compose the id when the object its adopted */
  override def componentId = "/BaseUnitSourceInformation"

  def rootLocation: StrField                     = fields.field(RootLocation)
  def withRootLocation(value: String): this.type = set(RootLocation, value)

  def additionalLocations: Seq[LocationInformation] = fields.field(AdditionalLocations)
  def withAdditionalLocations(locations: Seq[LocationInformation]): this.type =
    setArray(AdditionalLocations, locations)

  override def meta: Obj = BaseUnitSourceInformationModel

}

object BaseUnitSourceInformation {
  def apply(): BaseUnitSourceInformation = apply(Annotations())

  def apply(annotations: Annotations): BaseUnitSourceInformation = new BaseUnitSourceInformation(Fields(), annotations)
}

class LocationInformation(val fields: Fields, val annotations: Annotations) extends AmfObject {

  /** Value , path + field value that is used to compose the id when the object its adopted */
  override def componentId = "/LocationInformation"

  def locationValue: StrField                = fields.field(BaseUnitModel.Location)
  def withLocation(value: String): this.type = set(BaseUnitModel.Location, value)

  def elements: Seq[StrField]                     = fields.field(Elements)
  def withElements(value: Seq[String]): this.type = set(Elements, value)

  override def meta: Obj = LocationInformationModel

}

object LocationInformation {
  def apply(): LocationInformation = apply(Annotations())

  def apply(annotations: Annotations): LocationInformation = new LocationInformation(Fields(), annotations)
}
