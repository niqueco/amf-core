package amf.client.model.domain

import amf.client.convert.CoreClientConverters
import amf.client.environment.Environment
import amf.client.execution.BaseExecutionEnvironment
import amf.client.model.StrField
import amf.client.validate.PayloadValidator
import amf.core.model.domain.{RecursiveShape => InternalRecursiveShape}
import amf.core.validation.{PayloadValidator => InternalPayloadValidator}
import amf.core.unsafe.PlatformSecrets
import amf.client.convert.CoreClientConverters._

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("model.domain.RecursiveShape")
case class RecursiveShape(private[amf] override val _internal: InternalRecursiveShape)
    extends Shape
    with ValidatorAware
    with PlatformSecrets {

  def fixpoint: StrField = _internal.fixpoint

  def withFixPoint(shapeId: String): this.type = {
    _internal.withFixPoint(shapeId)
    this
  }

  override def linkCopy(): Linkable = throw new Exception("Recursive shape cannot be linked")

  def payloadValidator(mediaType: String): ClientOption[PayloadValidator] = {

    _internal.payloadValidator(mediaType).asClient
  }

  def payloadValidator(mediaType: String, exec: BaseExecutionEnvironment): ClientOption[PayloadValidator] = {
    _internal.payloadValidator(mediaType, exec).asClient
  }

  def payloadValidator(mediaType: String, env: Environment): ClientOption[PayloadValidator] =
    _internal.payloadValidator(mediaType, env._internal).asClient

  def parameterValidator(mediaType: String): ClientOption[PayloadValidator] =
    _internal.parameterValidator(mediaType).asClient
  def parameterValidator(mediaType: String, exec: BaseExecutionEnvironment): ClientOption[PayloadValidator] =
    _internal.parameterValidator(mediaType, exec).asClient
  def parameterValidator(mediaType: String, env: Environment): ClientOption[PayloadValidator] =
    _internal.parameterValidator(mediaType, env._internal).asClient
}
