package amf.client.remod

import amf.core.model.document.{BaseUnit, Document}
import amf.core.validation.{AMFValidationReport, AMFValidationResult}

import scala.concurrent.Future

abstract class AMFSimpleCompiler {

  // parse and validate the resource. Returns the model not resolved (clone for validate).
  def compile: Future[AmfResult]
}

case class AmfResult(bu:BaseUnit, result: AMFValidationReport){


  def conforms = result.conforms

  def asDocument = bu match {
    case d:Document => d
    case _ => Document()
  }
}
