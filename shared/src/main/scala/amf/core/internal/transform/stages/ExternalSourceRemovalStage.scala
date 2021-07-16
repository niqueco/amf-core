package amf.core.internal.transform.stages

import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.internal.metamodel.domain.ExternalSourceElementModel
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.{DomainElement, ExternalSourceElement}
import amf.core.client.scala.transform.TransformationStep
import amf.core.internal.transform.stages.selectors.{ExternalSourceElementSelector, KnownElementIdSelector}

import scala.collection.mutable

class ExternalSourceRemovalStage(val visited: mutable.Set[String] = mutable.Set()) extends TransformationStep {

  override def transform(model: BaseUnit, errorHandler: AMFErrorHandler): BaseUnit = {
    val knownIdSelector = new KnownElementIdSelector(visited)
    model.transform(knownIdSelector || ExternalSourceElementSelector, transformation)(errorHandler)
  }

  private def transformation(element: DomainElement, cycle: Boolean): Option[DomainElement] = {
    element match {
      case ex: ExternalSourceElement =>
        ex.fields.removeField(ExternalSourceElementModel.ReferenceId)
        Some(ex)
      case _ => Some(element)
    }
  }
}
