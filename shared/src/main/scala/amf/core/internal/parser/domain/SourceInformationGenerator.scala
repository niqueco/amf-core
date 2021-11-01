package amf.core.internal.parser.domain

import amf.core.client.scala.model.document.FieldsFilter.All
import amf.core.client.scala.model.document.{BaseUnit, BaseUnitSourceInformation, LocationInformation}
import amf.core.client.scala.model.domain.DomainElement
import amf.core.internal.metamodel.document.BaseUnitSourceInformationModel

object SourceInformationGenerator {

  def generateSourceInformation(baseUnit: BaseUnit, rootLocation: String): Unit = {
    val iterator = baseUnit.iterator(fieldsFilter = All).collect {
      case element: DomainElement => element // TODO: use .filterType, requires making it compatible with Iterator
    }
    val locationToElements = iterator.foldLeft(Map.empty[String, List[String]]) {
      case (result, element) =>
        element.location() match {
          case Some(elemLoc) if elemLoc != rootLocation =>
            val newElements = result.get(elemLoc) match {
              case Some(list) => element.id :: list
              case None       => List(element.id)
            }
            result + (elemLoc -> newElements)
          case _ => result
        }
    }
    val result = BaseUnitSourceInformation().withRootLocation(rootLocation)
    baseUnit.withSourceInformation(result) // set here so source info node is adopted. Not ideal.
    val additionalLocations = locationToElements.zipWithIndex.map {
      case ((loc, elements), i) =>
        LocationInformation().withLocation(loc).withElements(elements).withId(result.id + s"/location_$i")
    }
    if (additionalLocations.nonEmpty)
      result.setArrayWithoutId(BaseUnitSourceInformationModel.AdditionalLocations, additionalLocations.toList)
  }

}
