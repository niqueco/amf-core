package amf.core.internal.transform.stages

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.{DomainElement, LinkNode}
import amf.core.client.scala.transform.TransformationStep
import amf.core.internal.transform.stages.helpers.{LinkNodeResolver, ModelReferenceResolver}
import amf.core.internal.transform.stages.selectors.{KnownElementIdSelector, LinkNodeSelector, LinkSelector}

import scala.collection.mutable

class LinkNodeResolutionStage(keepEditingInfo: Boolean, val visited: mutable.Set[String] = mutable.Set())
    extends TransformationStep {

  var modelResolver: Option[ModelReferenceResolver] = None

  override def transform(
      model: BaseUnit,
      errorHandler: AMFErrorHandler,
      configuration: AMFGraphConfiguration
  ): BaseUnit = {
    this.modelResolver = Some(new ModelReferenceResolver(model))
    val knownIdSelector = new KnownElementIdSelector(visited)
    model.transform(knownIdSelector || LinkSelector || LinkNodeSelector, transformation)(errorHandler)
  }

  private def transformation(element: DomainElement, cycle: Boolean): Option[DomainElement] = {
    element match {
      case ln: LinkNode => LinkNodeResolver.resolveDynamicLink(ln, modelResolver, keepEditingInfo)
      case _            => Some(element)
    }
  }
}
