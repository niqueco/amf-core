package amf.core.client.scala.printer

import amf.core.client.scala.AMFResult
import amf.core.client.scala.validation.AMFValidationReportPrinter
import amf.core.client.scala.validation.AMFValidationReportPrinter.DefaultMax
import org.mulesoft.common.io.Output
import org.mulesoft.common.io.Output._

import java.io.StringWriter

object AMFResultPrinter {

  def print(result: AMFResult, max: Int = DefaultMax): String = {
    val writer = new StringWriter
    print(result, writer, max)
    writer.toString
  }

  def print[W: Output](result: AMFResult, writer: W, max: Int): Unit = {
    writer.append(s"Model: ${result.baseUnit.id}\n")
    AMFValidationReportPrinter.printConformance(result, writer, max)
  }
}
