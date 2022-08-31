package amf.core.internal.plugins.document.graph.emitter

import amf.core.client.scala.config.RenderOptions
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.{AmfObject, ExternalSourceElement}
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.document.BaseUnitModel
import amf.core.internal.metamodel.domain.ExternalSourceElementModel
import amf.core.internal.plugins.document.graph.MetaModelHelper.fieldsFrom

trait ApplicableMetaFieldRenderProvider {
  def fieldsFor(element: AmfObject, renderOptions: RenderOptions): Seq[Field] =
    filterUnwantedElements(element, fieldsFrom(element.meta), renderOptions)

  private def filterUnwantedElements(element: AmfObject, fields: Seq[Field], options: RenderOptions) = {
    element match {
      case e: ExternalSourceElement if e.isLinkToSource && !options.rawFieldEmission =>
        fields.filter(f => f != ExternalSourceElementModel.Raw)
      case _: BaseUnit if !options.sourceInformation => fields.filter(f => f != BaseUnitModel.SourceInformation)
      case _                                         => fields
    }
  }

}
