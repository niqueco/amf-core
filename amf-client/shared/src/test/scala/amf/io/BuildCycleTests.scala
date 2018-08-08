package amf.io

import amf.core.client.ParsingOptions
import amf.core.emitter.RenderOptions
import amf.core.model.document.BaseUnit
import amf.core.parser.ParserContext
import amf.core.rdf.{RdfModel, RdfModelParser}
import amf.core.remote.{Amf, Hint, Vendor}
import amf.facades.{AMFCompiler, AMFRenderer, Validation}
import org.scalatest.Assertion

import scala.concurrent.Future

/**
  * Cycle tests using temporary file and directory creator
  */
trait BuildCycleTests extends FileAssertionTest {

  val basePath: String

  /** Compile source with specified hint. Dump to target and assert against same source file. */
  def cycle(source: String, hint: Hint): Future[Assertion] = cycle(source, hint, basePath)

  /** Compile source with specified hint. Dump to target and assert against same source file. */
  def cycle(source: String, hint: Hint, directory: String): Future[Assertion] =
    cycle(source, source, hint, hint.vendor, directory, None)

  /** Compile source with specified hint. Render to temporary file and assert against golden. */
  final def cycle(source: String,
                  golden: String,
                  hint: Hint,
                  target: Vendor,
                  directory: String = basePath,
                  validation: Option[Validation] = None,
                  useAmfJsonldSerialization: Boolean = true): Future[Assertion] = {

    val config = CycleConfig(source, golden, hint, target, directory)

    build(config, validation, useAmfJsonldSerialization)
      .map(transform(_, config))
      .flatMap(render(_, config, useAmfJsonldSerialization))
      .flatMap(writeTemporaryFile(golden))
      .flatMap(assertDifferences(_, config.goldenPath))
  }

  /** Method to parse unit. Override if necessary. */
  def build(config: CycleConfig, given: Option[Validation], useAmfJsonldSerialisation: Boolean): Future[BaseUnit] = {
    val validation: Future[Validation] = given match {
      case Some(validation: Validation) => Future { validation }
      case None                         => Validation(platform).map(_.withEnabledValidation(false))
    }
    validation.flatMap { v =>
      var options = if (!useAmfJsonldSerialisation) { ParsingOptions().withoutAmfJsonLdSerialization } else { ParsingOptions().withAmfJsonLdSerialization }
      options = options.withBaseUnitUrl("file://" + config.goldenPath)
      AMFCompiler(s"file://${config.sourcePath}", platform, config.hint, v, parsingOptions = options).build()
    }
  }

  /** Method for transforming parsed unit. Override if necessary. */
  def transform(unit: BaseUnit, config: CycleConfig): BaseUnit = unit

  /** Method to render parsed unit. Override if necessary. */
  def render(unit: BaseUnit, config: CycleConfig, useAmfJsonldSerialization: Boolean): Future[String] = {
    val target = config.target
    var options = RenderOptions().withSourceMaps
    options = if (!useAmfJsonldSerialization) options.withoutAmfJsonLdSerialization else options.withAmfJsonLdSerialization
    new AMFRenderer(unit, target, target.defaultSyntax, options).renderToString
  }

  /** Method for transforming parsed unit. Override if necessary. */
  def transformRdf(unit: BaseUnit, config: CycleConfig): RdfModel = {
    unit.toNativeRdfModel()
  }

  /** Method for transforming parsed unit. Override if necessary. */
  def transformThroughRdf(unit: BaseUnit, config: CycleConfig): BaseUnit = {
    val rdfModel = unit.toNativeRdfModel(RenderOptions().withSourceMaps)
    new RdfModelParser(platform)(ParserContext()).parse(rdfModel, unit.id)
  }

  /** Method to render parsed unit. Override if necessary. */
  def renderRdf(unit: RdfModel, config: CycleConfig): Future[String] = {
    Future {
      unit.toN3().split("\n").sorted.mkString("\n")
    }
  }

  /** Compile source with specified hint. Render to temporary file and assert against golden. */
  def cycleRdf(source: String,
               golden: String,
               hint: Hint,
               target: Vendor = Amf,
               directory: String = basePath,
               validation: Option[Validation] = None): Future[Assertion] = {

    val config = CycleConfig(source, golden, hint, target, directory)

    build(config, validation, useAmfJsonldSerialisation = true)
      .map(transformRdf(_, config))
      .flatMap(renderRdf(_, config))
      .flatMap(writeTemporaryFile(golden))
      .flatMap(assertDifferences(_, config.goldenPath))
  }

  case class CycleConfig(source: String, golden: String, hint: Hint, target: Vendor, directory: String) {
    val sourcePath: String = directory + source
    val goldenPath: String = directory + golden
  }

  def cycleFullRdf(source: String,
                   golden: String,
                   hint: Hint,
                   target: Vendor = Amf,
                   directory: String = basePath,
                   validation: Option[Validation] = None): Future[Assertion] = {

    val config = CycleConfig(source, golden, hint, target, directory)

    build(config, validation, useAmfJsonldSerialisation = true)
      .map(transformThroughRdf(_, config))
      .flatMap(render(_, config, useAmfJsonldSerialization = true))
      .flatMap(writeTemporaryFile(golden))
      .flatMap(assertDifferences(_, config.goldenPath))
  }
}
