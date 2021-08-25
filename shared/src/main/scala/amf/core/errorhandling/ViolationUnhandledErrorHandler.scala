package amf.core.errorhandling

import amf.core.annotations.LexicalInformation
import amf.core.validation.SeverityLevels

trait ViolationUnhandledErrorHandler extends ErrorHandler {

  override def reportConstraint(id: String,
                                node: String,
                                property: Option[String],
                                message: String,
                                lexical: Option[LexicalInformation],
                                level: String,
                                location: Option[String]): Unit = {
    if (level == SeverityLevels.VIOLATION) {
      throw new Exception(
          s"  Message: $message\n  Target: $node\nProperty: ${property.getOrElse("")}\n  Position: $lexical\n at location: $location")
    }
  }
}

object ViolationUnhandledErrorHandler extends ViolationUnhandledErrorHandler {}
