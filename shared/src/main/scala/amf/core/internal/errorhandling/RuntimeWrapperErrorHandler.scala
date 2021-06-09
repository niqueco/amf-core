package amf.core.internal.errorhandling

import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.validation.AMFValidationResult

abstract class RuntimeWrapperErrorHandler(parent: AMFErrorHandler) extends AMFErrorHandler {

  override def report(result: AMFValidationResult): Unit = parent.report(result)

}
