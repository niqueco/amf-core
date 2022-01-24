package amf.core.internal.convert

import amf.core.client.platform.model.document.{Document, ExternalFragment, Fragment, Module}
import amf.core.client.platform.model.domain._
import amf.core.internal.convert.CoreClientConverters._
import amf.core.internal.metamodel.document.{
  BaseUnitProcessingDataModel,
  BaseUnitSourceInformationModel,
  LocationInformationModel
}
import amf.core.internal.remote.Platform

/** Shared Core registrations. */
object CoreRegister extends UniqueInitializer {

  // TODO ARM remove when APIMF-3000 is done
  def register(): Unit = register(platform)

  def register(platform: Platform): Unit = if (shouldInitialize) {

    platform.registerWrapper(amf.core.internal.metamodel.document.ModuleModel) {
      case m: amf.core.client.scala.model.document.Module => Module(m)
    }
    platform.registerWrapper(amf.core.internal.metamodel.document.DocumentModel) {
      case m: amf.core.client.scala.model.document.Document => new Document(m)
    }
    platform.registerWrapper(amf.core.internal.metamodel.document.FragmentModel) {
      case f: amf.core.client.scala.model.document.Fragment => new Fragment(f)
    }
    platform.registerWrapper(amf.core.internal.metamodel.document.ExternalFragmentModel) {
      case f: amf.core.client.scala.model.document.ExternalFragment => ExternalFragment(f)
    }
    platform.registerWrapper(amf.core.internal.metamodel.domain.ExternalDomainElementModel) {
      case f: amf.core.client.scala.model.domain.ExternalDomainElement => ExternalDomainElement(f)
    }
    platform.registerWrapper(amf.core.internal.metamodel.domain.DomainElementModel) {
      case e: amf.core.client.scala.model.domain.DomainElement => asClient(e)
    }
    platform.registerWrapper(amf.core.internal.metamodel.domain.extensions.CustomDomainPropertyModel) {
      case e: amf.core.client.scala.model.domain.extensions.CustomDomainProperty => CustomDomainProperty(e)
    }
    platform.registerWrapper(amf.core.internal.metamodel.domain.extensions.DomainExtensionModel) {
      case e: amf.core.client.scala.model.domain.extensions.DomainExtension => DomainExtension(e)
    }
    platform.registerWrapper(amf.core.internal.metamodel.domain.extensions.ShapeExtensionModel) {
      case e: amf.core.client.scala.model.domain.extensions.ShapeExtension => ShapeExtension(e)
    }
    platform.registerWrapper(amf.core.internal.metamodel.domain.extensions.PropertyShapeModel) {
      case e: amf.core.client.scala.model.domain.extensions.PropertyShape => PropertyShape(e)
    }
    platform.registerWrapper(amf.core.internal.metamodel.domain.ObjectNodeModel) {
      case d: amf.core.client.scala.model.domain.ObjectNode => ObjectNodeMatcher.asClient(d)
    }
    platform.registerWrapper(amf.core.internal.metamodel.domain.ScalarNodeModel) {
      case d: amf.core.client.scala.model.domain.ScalarNode => ScalarNodeMatcher.asClient(d)
    }
    platform.registerWrapper(amf.core.internal.metamodel.domain.LinkNodeModel) {
      case d: amf.core.client.scala.model.domain.LinkNode => LinkNode(d)
    }
    // hack for variable value accessor
    platform.registerWrapper(amf.core.internal.metamodel.domain.ArrayNodeModel) {
      case d: amf.core.client.scala.model.domain.ArrayNode => ArrayNodeMatcher.asClient(d)
    }
    platform.registerWrapper(amf.core.internal.metamodel.domain.DataNodeModel) {
      case d: amf.core.client.scala.model.domain.DataNode => DataNodeMatcher.asClient(d)
    }
    platform.registerWrapper(amf.core.internal.metamodel.domain.templates.VariableValueModel) {
      case v: amf.core.client.scala.model.domain.templates.VariableValue => VariableValue(v)
    }
    platform.registerWrapper(BaseUnitProcessingDataModel) {
      case v: amf.core.client.scala.model.document.BaseUnitProcessingData =>
        new amf.core.client.platform.model.document.BaseUnitProcessingData(v)
    }
    platform.registerWrapper(BaseUnitSourceInformationModel) {
      case v: amf.core.client.scala.model.document.BaseUnitSourceInformation =>
        new amf.core.client.platform.model.document.BaseUnitSourceInformation(v)
    }
    platform.registerWrapper(LocationInformationModel) {
      case v: amf.core.client.scala.model.document.LocationInformation =>
        new amf.core.client.platform.model.document.LocationInformation(v)
    }
  }

}
