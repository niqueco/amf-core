package amf.core.internal.errorhandling

import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.internal.validation.CoreValidations.SyamlWarning
import org.mulesoft.lexer.SourceLocation
import org.yaml.model.{DefaultJsonErrorHandler, SyamlException}

case class JsonErrorHandler(override val errorHandler: AMFErrorHandler) extends DefaultJsonErrorHandler {

  override protected def onIgnoredException(location: SourceLocation, e: SyamlException): Unit =
    errorHandler.warning(SyamlWarning, "", e.getMessage, location)
}
