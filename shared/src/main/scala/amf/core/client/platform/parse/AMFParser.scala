package amf.core.client.platform.parse

import amf.core.client.platform.{AMFGraphConfiguration, AMFObjectResult, AMFParseResult}
import amf.core.client.scala.parse.{AMFParser => InternalAMFParser}
import amf.core.internal.convert.CoreClientConverters._

import scala.concurrent.ExecutionContext
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportTopLevel("AMFParser")
@JSExportAll
object AMFParser {

  /** Asynchronously generate a BaseUnit from the unit located in the given url.
    * @param url
    *   Location of the api
    * @param configuration
    *   [[AMFGraphConfiguration]]
    * @return
    *   A CompletableFuture of [[AMFResult]]
    */
  def parse(url: String, configuration: AMFGraphConfiguration): ClientFuture[AMFParseResult] = {
    implicit val context: ExecutionContext = configuration._internal.getExecutionContext
    InternalAMFParser.parse(url, configuration).asClient
  }

  /** Asynchronously generate a BaseUnit from a given string.
    * @param content
    *   The unit to parse as a string
    * @param configuration
    *   [[AMFGraphConfiguration]]
    * @return
    *   A CompletableFuture of [[AMFResult]]
    */
  def parseContent(content: String, configuration: AMFGraphConfiguration): ClientFuture[AMFParseResult] = {
    implicit val context: ExecutionContext = configuration._internal.getExecutionContext
    InternalAMFParser.parseContent(content, configuration).asClient
  }

  /** Asynchronously generate a BaseUnit from a given string.
    * @param content
    *   The unit as a string
    * @param mediaType
    *   The nature and format of the given content e.g. <code>application/yaml</code> or <code>application/json</code>
    * @param configuration
    *   [[AMFGraphConfiguration]]
    * @return
    *   A CompletableFuture of [[AMFResult]]
    */
  def parseContent(
      content: String,
      mediaType: String,
      configuration: AMFGraphConfiguration
  ): ClientFuture[AMFParseResult] = {
    implicit val context: ExecutionContext = configuration._internal.getExecutionContext
    InternalAMFParser.parseContent(content, mediaType, configuration).asClient
  }

  def parseStartingPoint(
      graphUrl: String,
      startingPoint: String,
      configuration: AMFGraphConfiguration
  ): ClientFuture[AMFObjectResult] = {
    implicit val context: ExecutionContext = configuration._internal.getExecutionContext
    InternalAMFParser.parseStartingPoint(graphUrl, startingPoint, configuration).asClient
  }
}
