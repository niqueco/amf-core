package amf.emit

import amf.apicontract.client.scala.{AsyncAPIConfiguration, RAMLConfiguration}
import amf.apicontract.client.scala.{AMFConfiguration, OASConfiguration}
import amf.core.client.common.validation._
import amf.core.client.scala.errorhandling.{DefaultErrorHandler, UnhandledErrorHandler}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.common.transform._
import amf.core.client.scala.transform.TransformationPipeline
import amf.core.client.scala.validation.AMFValidationReport
import amf.core.internal.remote.Syntax.Syntax
import amf.core.internal.remote._
import amf.core.internal.unsafe.PlatformSecrets
import amf.io.FunSuiteCycleTests
import amf.testing.ConfigProvider.configFor
import amf.testing.HintProvider.defaultHintFor
import org.mulesoft.common.io.AsyncFile
import org.scalatest.Matchers

import scala.concurrent.Future
import scala.concurrent.Future.successful

class CompatibilityCycledValidationTest extends CompatibilityCycle {

  override val basePath = "amf-cli/shared/src/test/resources/compatibility/"

  testCycleCompatibility("oas30", Oas30JsonHint, Raml10, basePath)
  testCycleCompatibility("oas20", Oas20JsonHint, Raml10, basePath)
  testCycleCompatibility("raml10", Raml10YamlHint, Oas30, basePath)
  testCycleCompatibility("raml10", Raml10YamlHint, Oas20, basePath)
}

trait CompatibilityCycle extends FunSuiteCycleTests with Matchers with PlatformSecrets {

  def testCycleCompatibility(filePath: String,
                             from: Hint,
                             to: Spec,
                             basePath: String,
                             syntax: Option[Syntax] = None,
                             pipeline: Option[String] = None): Unit = {
    for {
      file <- platform.fs.syncFile(basePath + filePath).list.sorted
    } {
      val path = s"$filePath/$file"

      test(s"Test $path to $to") {
        val config       = CycleConfig(path, path, from, defaultHintFor(to), basePath, pipeline)
        val targetHint   = hint(spec = to)
        val toProfile    = profile(to)
        val amfConfig    = buildConfig(None, None)
        val targetConfig = buildConfig(amfConfigFrom(to), None, None)
        for {
          origin   <- build(config, amfConfig)
          resolved <- successful(transform(origin, config, targetConfig))
          rendered <- successful(render(resolved, config, targetConfig))
          tmp      <- writeTemporaryFile(path)(rendered)
          report   <- validate(tmp, to)
        } yield {
          report.conforms shouldBe true
        }
      }
    }
  }

  private def amfConfigFrom(spec: Spec): AMFConfiguration = spec match {
    case Spec.OAS30   => OASConfiguration.OAS30()
    case Spec.OAS20   => OASConfiguration.OAS20()
    case Spec.RAML10  => RAMLConfiguration.RAML10()
    case Spec.RAML08  => RAMLConfiguration.RAML08()
    case Spec.ASYNC20 => AsyncAPIConfiguration.Async20()
    case _            => throw new IllegalArgumentException
  }

  private def hint(spec: Spec) = spec match {
    case Raml10 => Raml10YamlHint
    case Raml08 => Raml08YamlHint
    case Oas20  => Oas20YamlHint
    case Oas30  => Oas30YamlHint
    case _      => throw new IllegalArgumentException
  }

  private def validate(source: AsyncFile, spec: Spec): Future[AMFValidationReport] = {
    val handler   = DefaultErrorHandler()
    val amfConfig = buildConfig(configFor(spec), None, Some(handler))
    build(source.path, source.path, amfConfig).flatMap { unit =>
      amfConfig.baseUnitClient().validate(unit)
    }
  }

  override def transform(unit: BaseUnit, config: CycleConfig, amfConfig: AMFConfiguration): BaseUnit = {
    amfConfig
      .withErrorHandlerProvider(() => UnhandledErrorHandler)
      .baseUnitClient()
      .transform(unit, PipelineId.Compatibility)
      .baseUnit
  }

  private def profile(spec: Spec): ProfileName = spec match {
    case Raml10 => Raml10Profile
    case Raml08 => Raml08Profile
    case Oas20  => Oas20Profile
    case Oas30  => Oas30Profile
    case _      => throw new IllegalArgumentException
  }
}
