package amf.core.internal.plugins.document.graph.emitter

import amf.core.client.scala.config.RenderOptions
import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.metamodel.Field

object InstanceMetaApplicableFieldRenderProvider extends ApplicableMetaFieldRenderProvider {
  override def fieldsFor(element: AmfObject, renderOptions: RenderOptions): Seq[Field] =
    super.fieldsFor(element, renderOptions)
}
