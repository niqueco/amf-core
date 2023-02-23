package amf.core.client.scala.validation

import amf.core.client.common.validation.{ProfileName, SeverityLevels, UnknownProfile}
import amf.core.client.scala.AMFResult

case class AMFValidationReport(model: String, profile: ProfileName, override val results: Seq[AMFValidationResult])
    extends ReportConformance(results) {

  private val DefaultMax = 30

  def toString(max: Int): String = AMFValidationReportPrinter.print(this, max)

  override def toString: String = toString(DefaultMax)

  def merge(report: AMFValidationReport): AMFValidationReport =
    AMFValidationReport(report.model, report.profile, results ++ report.results)
}

object AMFValidationReport {
  def apply(model: String, profile: ProfileName, results: Seq[AMFValidationResult]) =
    new AMFValidationReport(model, profile, results)

  def empty(model: String, profileName: ProfileName): AMFValidationReport = apply(model, profileName, Seq())

  /** used for storing results of parsing and transformation into report for tests */
  private[amf] def unknownProfile(result: AMFResult): AMFValidationReport = {
    val profileName = UnknownProfile
    val model       = result.baseUnit
    new AMFValidationReport(
      model.location().getOrElse(model.id),
      profileName,
      result.results
    )
  }
}

abstract class ReportConformance(val results: Seq[AMFValidationResult]) {
  lazy val conforms: Boolean = !results.exists(r => r.severityLevel == SeverityLevels.VIOLATION)
}
