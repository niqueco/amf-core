package amf.core.internal.transform.stages

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.document.FieldsFilter.All
import amf.core.client.scala.model.document.{BaseUnit, BaseUnitSourceInformation, LocationInformation}
import amf.core.client.scala.model.domain.DomainElement
import amf.core.client.scala.parse.AMFParser
import amf.core.client.scala.transform.TransformationStep
import amf.core.internal.metamodel.document.BaseUnitSourceInformationModel

object SourceInformationStage extends TransformationStep {

  override def transform(model: BaseUnit,
                         errorHandler: AMFErrorHandler,
                         configuration: AMFGraphConfiguration): BaseUnit = {
    val rootLocation                                       = model.location().getOrElse(AMFParser.DEFAULT_DOCUMENT_URL)
    val domainElements                                     = model.iterator(fieldsFilter = All).collect { case element: DomainElement => element }
    val locationToElements: Map[Location, List[ElementId]] = buildLocationMap(domainElements, rootLocation)

    val result = BaseUnitSourceInformation().withRootLocation(rootLocation)
    model.withSourceInformation(result) // set here so source info node is adopted. Not ideal.
    val additionalLocations = locationToElements.zipWithIndex.map {
      case ((loc, elements), i) =>
        LocationInformation().withLocation(loc).withElements(elements).withId(result.id + s"/location_$i")
    }
    if (additionalLocations.nonEmpty)
      result.setArrayWithoutId(BaseUnitSourceInformationModel.AdditionalLocations, additionalLocations.toList)
    model
  }

  type Location  = String
  type ElementId = String
  private def buildLocationMap(iterator: Iterator[DomainElement],
                               rootLocation: String): Map[Location, List[ElementId]] = {
    iterator.foldLeft(Map.empty[String, List[String]]) {
      case (result, element) =>
        element.location() match {
          case Some(elemLoc) if elemLoc != rootLocation =>
            val updatedElements = result.get(elemLoc) match {
              case Some(list) => element.id :: list
              case None       => List(element.id)
            }
            result + (elemLoc -> updatedElements)
          case _ => result
        }
    }
  }
}
