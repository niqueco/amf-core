package amf.client.remod.amfcore.config

/**
  * Immutable implementation of parsing options
  */
case class ParsingOptions private[amf] (amfJsonLdSerialization: Boolean = true,
                                        baseUnitUrl: Option[String] = None,
                                        maxYamlReferences: Option[Long] = None) {

  /** Parse specific AMF JSON-LD serialization */
  def withoutAmfJsonLdSerialization: ParsingOptions = copy(amfJsonLdSerialization = false)

  /** Parse regular JSON-LD serialization */
  def withAmfJsonLdSerialization: ParsingOptions = copy(amfJsonLdSerialization = true)

  /** Include the BaseUnit Url */
  def withBaseUnitUrl(baseUnit: String): ParsingOptions = copy(baseUnitUrl = Some(baseUnit))

  /** Exclude the BaseUnit Url */
  def withoutBaseUnitUrl(): ParsingOptions = copy(baseUnitUrl = None)

  /** Defines an upper bound of yaml alias that will be resolved when parsing a DataNode */
  def setMaxYamlReferences(value: Long): ParsingOptions = copy(maxYamlReferences = Some(value))

  def isAmfJsonLdSerialization: Boolean  = amfJsonLdSerialization
  def definedBaseUrl: Option[String]     = baseUnitUrl
  def getMaxYamlReferences: Option[Long] = maxYamlReferences

}
