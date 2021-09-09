package amf.core.client.scala.errorhandling

import amf.core.internal.annotations.LexicalInformation

/** Error handler that throws Exception when a constraint is reported */
trait UnhandledErrorHandler extends AMFErrorHandler {

  override def reportConstraint(id: String,
                                node: String,
                                property: Option[String],
                                message: String,
                                lexical: Option[LexicalInformation],
                                level: String,
                                location: Option[String]): Unit = {
    throw new Exception(
        s"  Message: $message\n  Target: $node\nProperty: ${property.getOrElse("")}\n  Position: $lexical\n at location: $location")
  }
}

object UnhandledErrorHandler extends UnhandledErrorHandler {}
