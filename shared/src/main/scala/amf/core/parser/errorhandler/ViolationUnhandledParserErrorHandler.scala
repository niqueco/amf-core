package amf.core.parser.errorhandler

import amf.core.AMFCompilerRunCount
import amf.core.validation.{AMFValidationResult, SeverityLevels}

object ViolationUnhandledParserErrorHandler extends ViolationUnhandledAmfParserErrorHandler {}

trait ViolationUnhandledAmfParserErrorHandler extends AmfParserErrorHandler {
  override val parserRun: Int = AMFCompilerRunCount.NONE
  override def handlerAmfResult(result: AMFValidationResult): Boolean = {
    if (result.severityLevel == SeverityLevels.VIOLATION) {
      throw new Exception(
          s"  Message: ${result.message}\n  Target: ${result.targetNode}\nProperty: ${result.targetProperty
            .getOrElse("")}\n  Position: ${result.position}\n at location: ${result.location}")
    } else false
  }
}
