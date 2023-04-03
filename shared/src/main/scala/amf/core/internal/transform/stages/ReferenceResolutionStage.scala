package amf.core.internal.transform.stages

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.internal.metamodel.document.DocumentModel
import amf.core.client.scala.model.document.{BaseUnit, Document}
import amf.core.client.scala.model.domain._
import amf.core.client.scala.transform.TransformationStep
import amf.core.internal.transform.stages.elements.resolution.{
  ElementResolutionStage,
  ElementStageTransformer,
  ReferenceResolution
}
import amf.core.internal.transform.stages.helpers.ModelReferenceResolver
import amf.core.internal.transform.stages.selectors.{LinkNodeSelector, LinkSelector}

import scala.collection.mutable

class ReferenceResolutionStage(keepEditingInfo: Boolean) extends TransformationStep {
  override def transform(
      model: BaseUnit,
      errorHandler: AMFErrorHandler,
      configuration: AMFGraphConfiguration
  ): BaseUnit = {
    new ReferenceResolutionInnerClass()(errorHandler).transform(model, configuration)
  }

  // TODO should be in an Adapter specific for ExtendsResolution
  def resolveDomainElement[T <: DomainElement](
      element: T,
      errorHandler: AMFErrorHandler,
      configuration: AMFGraphConfiguration
  ): T = {
    val doc = Document().withId("http://resolutionstage.com/test#")
    if (element.id != null) {
      doc.fields.setWithoutId(DocumentModel.Encodes, element)
    } else {
      doc.withEncodes(element)
    }
    transform(doc, errorHandler, configuration)
    doc.encodes.asInstanceOf[T]
  }

  protected def customDomainElementTransformation: (DomainElement, Linkable) => (DomainElement, Boolean) =
    (d: DomainElement, _: Linkable) => (d, false)

  private class ReferenceResolutionInnerClass(implicit val errorHandler: AMFErrorHandler)
      extends ElementResolutionStage[DomainElement] {

    var modelResolver: Option[ModelReferenceResolver] = None
    val cache: mutable.Map[String, DomainElement]     = mutable.Map()

    def transform[T <: BaseUnit](model: T, configuration: AMFGraphConfiguration): T = {
      this.modelResolver = Some(new ModelReferenceResolver(model))
      model
        .transform(
          LinkSelector || LinkNodeSelector,
          (element: DomainElement, isCycle: Boolean) => transformation(element, isCycle, configuration)
        )
        .asInstanceOf[T]
    }

    private def transformation(
        element: DomainElement,
        isCycle: Boolean,
        configuration: AMFGraphConfiguration
    ): Option[DomainElement] =
      transformer.transform(element, configuration)

    override def transformer: ElementStageTransformer[DomainElement] =
      new ReferenceResolution(
        cache = cache,
        keepEditingInfo = keepEditingInfo,
        modelResolver = modelResolver,
        errorHandler = errorHandler,
        customDomainElementTransformation = customDomainElementTransformation
      )
  }
}
