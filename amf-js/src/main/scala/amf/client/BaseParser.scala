package amf.client

import amf.ProfileNames
import amf.framework.validation.AMFValidationReport
import amf.remote.Syntax.Syntax
import amf.remote._
import amf.plugins.document.webapi.model.{Extension => CoreExtension, Overlay => CoreOverlay}
import amf.framework.model.document.{BaseUnit => CoreBaseUnit, Document => CoreDocument, Fragment => CoreFragment, Module => CoreModule}
import amf.model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSExport

/**
  * Base class for JS parsers.
  */
abstract class BaseParser(protected val vendor: Vendor, protected val syntax: Syntax) extends PlatformParser {

  private val DEFAULT_DOCUMENT_URL = "http://raml.org/amf/default_document"

  private def unitScalaToJS(unit: CoreBaseUnit): BaseUnit = unit match {
    case o: CoreOverlay           => new Overlay(o)
    case e: CoreExtension         => new Extension(e)
    case d: CoreDocument          => Document(d)
    case m: CoreModule            => Module(m)
    case f: CoreFragment          => Fragment(f)
  }

  /**
    * Generates a [[amf.model.BaseUnit]] from the api located in the given url.
    * @param url : Location of the api.
    * @param handler Handler object to execute the success or fail functions with the result object model.
    */
  @JSExport
  def parseFile(url: String, handler: JsHandler[BaseUnit]): Unit =
    super.parse(url, BaseUnitHandlerAdapter(handler))

  /**
    * Generates the [[amf.model.BaseUnit]] from a given string, which should be a valid api.
    * @param stream: The api as a string.
    * @param handler Handler object to execute the success or fail functions with the result object model.
    */
  @JSExport
  def parseString(stream: String, handler: JsHandler[BaseUnit]): Unit =
    super.parse(DEFAULT_DOCUMENT_URL,
                BaseUnitHandlerAdapter(handler),
                Some(StringContentPlatform(DEFAULT_DOCUMENT_URL, stream, platform)))

  /**
    * Asynchronously generate a [[amf.model.BaseUnit]] from the api located in the given url.
    * @param url : Location of the api.
    * @return A js promise that will have a [[amf.model.BaseUnit]] or an error to handle the result of such invocation.
    */
  @JSExport
  def parseFileAsync(url: String): js.Promise[BaseUnit] =
    super.parseAsync(url).map(unitScalaToJS).toJSPromise

  /**
    * Asynchronously generate a [[amf.model.BaseUnit]] from a given string, which should be a valid api.
    * @param stream: The api as a string
    * @return A js promise that will have a [[amf.model.BaseUnit]] or an error to handle the result of such invocation.
    */
  @JSExport
  def parseStringAsync(stream: String): js.Promise[BaseUnit] =
    super
      .parseAsync(DEFAULT_DOCUMENT_URL, Some(StringContentPlatform(DEFAULT_DOCUMENT_URL, stream, platform)))
      .map(unitScalaToJS)
      .toJSPromise

  @JSExport
  def parseStringAsync(stream: String, platform: Platform): js.Promise[BaseUnit] =
    super
      .parseAsync(DEFAULT_DOCUMENT_URL, Some(StringContentPlatform(DEFAULT_DOCUMENT_URL, stream, platform)))
      .map(unitScalaToJS)
      .toJSPromise

  @JSExport
  def parseStringAsync(textUrl: String, stream: String, platform: Platform): js.Promise[BaseUnit] =
    super.parseAsync(textUrl, Some(StringContentPlatform(textUrl, stream, platform))).map(unitScalaToJS).toJSPromise

  /**
    * Generates the validation report for the last parsed model.
    * @param profileName name of the profile to be parsed
    * @param messageStyle if a RAML/OAS profile, this can be set to the preferred error reporting styl
    * @return the AMF validation report
    */
  @JSExport
  def reportValidation(profileName: String, messageStyle: String): js.Promise[AMFValidationReport] =
    super.reportValidationImplementation(profileName, messageStyle).toJSPromise

  def reportValidation(profileName: String): js.Promise[AMFValidationReport] =
    super.reportValidationImplementation(profileName, ProfileNames.RAML).toJSPromise

  /**
    * Generates a custom validaton profile as specified in the input validation profile file
    * @param profileName name of the profile to be parsed
    * @param customProfilePath path to the custom profile file
    * @return the AMF validation report
    */
  @JSExport
  def reportCustomValidation(profileName: String, customProfilePath: String): js.Promise[AMFValidationReport] =
    super.reportCustomValidationImplementation(profileName, customProfilePath).toJSPromise

  private case class BaseUnitHandlerAdapter(handler: JsHandler[BaseUnit]) extends Handler[amf.framework.model.document.BaseUnit] {
    override def success(document: amf.framework.model.document.BaseUnit): Unit = handler.success(unitScalaToJS(document))
    override def error(exception: Throwable): Unit              = handler.error(exception)
  }
}

/** Trait that needs to be implemented to handle different kinds of results. */
@js.native
trait JsHandler[T] extends js.Object {
  def success(document: T): Unit = js.native

  def error(exception: Throwable): Unit = js.native
}
