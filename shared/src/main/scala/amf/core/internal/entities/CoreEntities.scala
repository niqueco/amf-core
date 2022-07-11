package amf.core.internal.entities

import amf.core.internal.metamodel.ModelDefaultBuilder
import amf.core.internal.metamodel.document._
import amf.core.internal.metamodel.domain.extensions._
import amf.core.internal.metamodel.domain.federation.{FederationMetadataModel, ShapeFederationMetadataModel}
import amf.core.internal.metamodel.domain.templates.VariableValueModel
import amf.core.internal.metamodel.domain.{ExternalDomainElementModel, RecursiveShapeModel}

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
      BaseUnitProcessingDataModel,
      BaseUnitSourceInformationModel,
      LocationInformationModel,
      PropertyShapePathModel,
      ShapeFederationMetadataModel
  )
}
