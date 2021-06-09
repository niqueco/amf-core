package amf.core.internal

import amf.core.client.scala.validation.AMFValidationReport

abstract class AMFResultLike[T](element: T, report: AMFValidationReport) {
  def conforms: Boolean = report.conforms

}
