package amf.core.internal.validation

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.validation
import amf.core.client.scala.validation.{AMFValidationReport, AMFValidationResult}
import amf.core.client.common.validation._
import amf.core.client.scala.vocabulary.Namespace
import amf.core.internal.annotations.LexicalInformation
import amf.core.internal.validation.core.ValidationProfile.SeverityLevel
import amf.core.internal.validation.core.{ValidationReport, ValidationResult, ValidationSpecification}

import scala.collection.mutable

trait ShaclReportAdaptation {

  protected def adaptToAmfReport(model: BaseUnit,
                                 profile: ProfileName,
                                 report: ValidationReport,
                                 validations: EffectiveValidations): AMFValidationReport = {
    val amfResults = report.results.flatMap { r =>
      adaptToAmfResult(model, r, profile.messageStyle, validations)
    }
    validation.AMFValidationReport(model.id, profile, amfResults)
  }

  protected def adaptToAmfReport(model: BaseUnit,
                                 profile: ProfileName,
                                 report: ValidationReport,
                                 location: Option[String],
                                 lexical: LexicalInformation): AMFValidationReport = {
    val amfResults = report.results.map { result =>
      AMFValidationResult.fromSHACLValidation(model.id, result, location, lexical)
    }
    AMFValidationReport(model.id, profile, amfResults)
  }

  protected def adaptToAmfResult(model: BaseUnit,
                                 result: ValidationResult,
                                 messageStyle: MessageStyle,
                                 validations: EffectiveValidations): Option[AMFValidationResult] = {
    val validationSpecToLook = if (result.sourceShape.startsWith(Namespace.Data.base)) {
      result.sourceShape
        .replace(Namespace.Data.base, "") // this is for custom validations they are all prefixed with the data namespace
    } else {
      result.sourceShape // by default we expect to find a URI here
    }
    val idMapping: mutable.HashMap[String, String] = mutable.HashMap()
    val maybeTargetSpecification: Option[ValidationSpecification] = validations.all.get(validationSpecToLook) match {
      case Some(validationSpec) =>
        idMapping.put(result.sourceShape, validationSpecToLook)
        Some(validationSpec)

      case None =>
        validations.all.find {
          case (v, _) =>
            // processing property shapes Id computed as constraintID + "/prop"

            validationSpecToLook.startsWith(v)
        } match {
          case Some((v, spec)) =>
            idMapping.put(result.sourceShape, v)
            Some(spec)
          case None =>
            if (validationSpecToLook.startsWith("_:")) {
              None
            } else {
              throw new Exception(s"Cannot find validation spec for validation error:\n $result")
            }
        }
    }
    maybeTargetSpecification.map { spec =>
      val message: String        = computeMessage(spec, result, messageStyle)
      val finalId: SeverityLevel = computeValidationId(result, idMapping)
      AMFValidationResult.withShapeId(finalId,
                                      AMFValidationResult.fromSHACLValidation(model, message, result.severity, result))
    }
  }

  private def computeMessage(spec: ValidationSpecification, result: ValidationResult, style: MessageStyle) = {
    var message = style match {
      case RAMLStyle => spec.ramlMessage.getOrElse(spec.message)
      case OASStyle  => spec.oasMessage.getOrElse(spec.message)
      case _         => result.message.getOrElse(Option(spec.message).getOrElse(""))
    }

    if (Option(message).isEmpty || message == "") {
      message = result.message.getOrElse("Constraint violation")
    }
    if (spec.isParserSide && result.message.nonEmpty) {
      message = result.message.get
    }
    message
  }

  private def computeValidationId(result: ValidationResult, idMapping: mutable.HashMap[String, String]) = {
    val finalId = if (idMapping(result.sourceShape).startsWith("http")) {
      idMapping(result.sourceShape)
    } else {
      Namespace.Data.base + idMapping(result.sourceShape)
    }
    finalId
  }

  protected def findLevel(id: String,
                          validations: EffectiveValidations,
                          default: String = SeverityLevels.VIOLATION): SeverityLevel =
    validations.findSecurityLevelFor(id).getOrElse(SeverityLevels.unapply(default))

}
