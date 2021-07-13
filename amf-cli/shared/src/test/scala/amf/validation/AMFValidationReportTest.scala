package amf.validation

import amf.core.client.common.validation.Raml10Profile
import amf.core.client.scala.validation.{AMFValidationReport, AMFValidationResult}
import org.scalatest.FunSuite

class AMFValidationReportTest extends FunSuite {

  test("Test to string report with null values in results") {

    //noinspection ScalaStyle
    AMFValidationReport(
      null,
      Raml10Profile,
      Seq(
        AMFValidationResult(null, null, null, None, null, null, None, null),
        AMFValidationResult(null, null, null, None, null, None, None, null)
      )
    ).toString
  }
}