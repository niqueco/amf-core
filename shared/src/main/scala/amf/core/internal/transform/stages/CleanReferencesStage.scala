package amf.core.internal.transform.stages

import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.internal.annotations.References
import amf.core.internal.metamodel.document.BaseUnitModel
import amf.core.client.scala.model.document.{BaseUnit, Fragment, Module}
import amf.core.client.scala.transform.TransformationStep

class CleanReferencesStage() extends TransformationStep {
  override def transform(model: BaseUnit, errorHandler: AMFErrorHandler): BaseUnit = {
    persistReferenceShapes(model)

    model.fields.removeField(BaseUnitModel.References)
    model
  }

  private def persistReferenceShapes[T <: BaseUnit](model: T): Unit = {
    val referenceShapes = model.references.collect {
      case fragment: Fragment => Option(fragment.encodes).map(_.id).toList
      case module: Module     => module.declares.map(_.id)
    }.flatten
    model.annotations += References(referenceShapes)
  }
}
