package amf.core.internal.transform.stages

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.internal.annotations.Declares
import amf.core.internal.metamodel.document.DocumentModel
import amf.core.client.scala.model.document.{BaseUnit, DeclaresModel, EncodesModel}
import amf.core.client.scala.model.domain.AmfArray
import amf.core.client.scala.transform.TransformationStep

class DeclarationsRemovalStage() extends TransformationStep {

  override def transform(
      model: BaseUnit,
      errorHandler: AMFErrorHandler,
      configuration: AMFGraphConfiguration
  ): BaseUnit = {
    model match {
      case doc: DeclaresModel with EncodesModel => removeAllDeclarationsButSecuritySchemes(doc)
      case _                                    => // ignore
    }
    model
  }

  private def removeAllDeclarationsButSecuritySchemes(doc: DeclaresModel) = {
    val schemes = doc.declares.filter(_.meta.`type`.head.iri() == "http://a.ml/vocabularies/security#SecurityScheme")
    persistDeclaredShapes(doc)
    if (schemes.isEmpty) {
      doc.fields.removeField(DocumentModel.Declares)
    } else {
      doc.fields.?[AmfArray](DocumentModel.Declares) match {
        case Some(array) => array.values = schemes
        case _           =>
      }
    }
  }

  private def persistDeclaredShapes(doc: DeclaresModel): Unit = {
    val declaredShapes = doc.declares.map(_.id)
    doc.annotations += Declares(declaredShapes)
  }
}
