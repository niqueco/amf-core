package amf.core.internal.convert

import amf.core.client.common.validation.ValidationMode
import amf.core.client.platform.model.domain.{Shape => ClientShape}
import amf.core.client.platform.validation.payload.{
  AMFShapePayloadValidationPlugin => ClientAMFShapePayloadValidationPlugin,
  AMFShapePayloadValidator => ClientAMFShapePayloadValidator
}
import amf.core.client.platform.validation.{payload, AMFValidationReport => ClientValidationReport}
import amf.core.client.scala.model.document.PayloadFragment
import amf.core.client.scala.model.domain.Shape
import amf.core.client.scala.validation.AMFValidationReport
import amf.core.client.scala.validation.payload.{
  AMFShapePayloadValidationPlugin,
  AMFShapePayloadValidator,
  ShapeValidationConfiguration,
  ValidatePayloadRequest
}
import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.model.document.{PayloadFragment => ClientPayloadFragment}

import scala.concurrent.{ExecutionContext, Future}

object PayloadValidationPluginConverter {
  implicit object PayloadValidationPluginMatcher
      extends BidirectionalMatcherWithEC[AMFShapePayloadValidationPlugin, ClientAMFShapePayloadValidationPlugin] {

    override def asInternal(
        from: ClientAMFShapePayloadValidationPlugin
    )(implicit executionContext: ExecutionContext): AMFShapePayloadValidationPlugin = {
      new AMFShapePayloadValidationPlugin {
        override def applies(element: ValidatePayloadRequest): Boolean =
          from.applies(payload.ValidatePayloadRequest(element))

        override def validator(
            shape: Shape,
            mediaType: String,
            config: ShapeValidationConfiguration,
            validationMode: ValidationMode
        ): AMFShapePayloadValidator = {
          val clientValidator = from.validator(
              platform.wrap[ClientShape](shape),
              mediaType,
              new payload.ShapeValidationConfiguration(config),
              validationMode
          )
          new AMFShapePayloadValidator {
            override def validate(payload: String): Future[AMFValidationReport] =
              clientValidator.validate(payload).asInternal

            override def validate(payloadFragment: PayloadFragment): Future[AMFValidationReport] =
              clientValidator.validate(PayloadFragmentMatcher.asClient(payloadFragment)).asInternal

            override def syncValidate(payload: String): AMFValidationReport =
              ValidationReportMatcher.asInternal(clientValidator.syncValidate(payload))
          }
        }

        override val id: String = from.id
      }
    }

    override def asClient(
        from: AMFShapePayloadValidationPlugin
    )(implicit executionContext: ExecutionContext): ClientAMFShapePayloadValidationPlugin = {
      new ClientAMFShapePayloadValidationPlugin {
        override def applies(element: payload.ValidatePayloadRequest): Boolean = from.applies(element._internal)

        override def validator(
            shape: ClientShape,
            mediaType: String,
            config: payload.ShapeValidationConfiguration,
            validationMode: ValidationMode
        ): ClientAMFShapePayloadValidator = {
          val validator = from.validator(shape._internal, mediaType, config._internal, validationMode)
          new ClientAMFShapePayloadValidator {
            override def validate(payload: String): CoreClientConverters.ClientFuture[ClientValidationReport] =
              validator.validate(payload).asClient

            override def validate(
                payloadFragment: ClientPayloadFragment
            ): CoreClientConverters.ClientFuture[ClientValidationReport] =
              validator.validate(PayloadFragmentMatcher.asInternal(payloadFragment)).asClient

            override def syncValidate(payload: String): ClientValidationReport =
              ValidationReportMatcher.asClient(validator.syncValidate(payload))
          }
        }

        override val id: String = from.id
      }
    }
  }
}

object PayloadValidatorConverter {
  implicit object PayloadValidatorMatcher
      extends BidirectionalMatcherWithEC[AMFShapePayloadValidator, ClientAMFShapePayloadValidator] {

    override def asClient(
        from: AMFShapePayloadValidator
    )(implicit executionContext: ExecutionContext): ClientAMFShapePayloadValidator = {
      new ClientAMFShapePayloadValidator {
        override def validate(payload: String): ClientFuture[ClientValidationReport] =
          InternalFutureOps(from.validate(payload)).asClient

        override def validate(payloadFragment: ClientPayloadFragment): ClientFuture[ClientValidationReport] =
          InternalFutureOps(from.validate(payloadFragment._internal)).asClient

        override def syncValidate(payload: String): ClientValidationReport =
          ValidationReportMatcher.asClient(from.syncValidate(payload))
      }
    }

    override def asInternal(
        from: ClientAMFShapePayloadValidator
    )(implicit executionContext: ExecutionContext): AMFShapePayloadValidator = {
      new AMFShapePayloadValidator {
        override def validate(payload: String): Future[AMFValidationReport] = from.validate(payload).asInternal

        override def validate(payloadFragment: PayloadFragment): Future[AMFValidationReport] =
          ClientFutureOps(from.validate(PayloadFragmentMatcher.asClient(payloadFragment))).asInternal

        override def syncValidate(payload: String): AMFValidationReport =
          ValidationReportMatcher.asInternal(from.syncValidate(payload))
      }
    }
  }
}
