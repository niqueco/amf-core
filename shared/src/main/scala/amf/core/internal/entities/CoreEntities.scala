package amf.core.internal.entities

import amf.core.internal.metamodel.{ModelDefaultBuilder, Obj}
import amf.core.internal.metamodel.document.{
  BaseUnitProcessingDataModel,
  DocumentModel,
  ExternalFragmentModel,
  ModuleModel,
  SourceMapModel
}
import amf.core.internal.metamodel.domain.{ExternalDomainElementModel, RecursiveShapeModel}
import amf.core.internal.metamodel.domain.extensions.{
  CustomDomainPropertyModel,
  DomainExtensionModel,
  PropertyShapeModel,
  ShapeExtensionModel
}
import amf.core.internal.metamodel.domain.templates.VariableValueModel

private[amf] object CoreEntities extends Entities {

  override protected val innerEntities: Seq[ModelDefaultBuilder] = Seq(
      DocumentModel,
      ModuleModel,
      VariableValueModel,
      RecursiveShapeModel,
      PropertyShapeModel,
      ShapeExtensionModel,
      CustomDomainPropertyModel,
      ExternalFragmentModel,
      ExternalDomainElementModel,
      DomainExtensionModel,
      BaseUnitProcessingDataModel
  )

}
