package amf.core.client.platform.config

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.scala.config
import amf.core.client.scala.config.{ParsingOptions => InternalParsingOptions}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
case class ParsingOptions(private[amf] val _internal: InternalParsingOptions) {

  @JSExportTopLevel("ParsingOptions")
  def this() = this(InternalParsingOptions())

  def isAmfJsonLdSerialization: Boolean       = _internal.amfJsonLdSerialization
  def definedBaseUrl: ClientOption[String]    = _internal.baseUnitUrl.asClient
  def getMaxYamlReferences: ClientOption[Int] = _internal.maxYamlReferences.asClient

  /** Parse specific AMF JSON-LD serialization */
  def withoutAmfJsonLdSerialization(): ParsingOptions = _internal.withoutAmfJsonLdSerialization

  /** Parse regular JSON-LD serialization */
  def withAmfJsonLdSerialization(): ParsingOptions = _internal.withAmfJsonLdSerialization

  /** Include the BaseUnit Url */
  def withBaseUnitUrl(baseUnit: String): ParsingOptions = _internal.withBaseUnitUrl(baseUnit)

  /** Exclude the BaseUnit Url */
  def withoutBaseUnitUrl(): ParsingOptions = _internal.withoutBaseUnitUrl

  /** Defines an upper bound of yaml alias that will be resolved when parsing a DataNode */
  def setMaxYamlReferences(value: Int): ParsingOptions = _internal.setMaxYamlReferences(value)
}
