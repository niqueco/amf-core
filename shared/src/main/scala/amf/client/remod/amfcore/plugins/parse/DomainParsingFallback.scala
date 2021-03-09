package amf.client.remod.amfcore.plugins.parse

import amf.core.model.document.{BaseUnit, ExternalFragment}
import amf.core.model.domain.ExternalDomainElement

private[amf] trait DomainParsingFallback {

  def chooseFallback(element: ParsingInfo, availablePlugins: Seq[AMFParsePlugin]): BaseUnit
}

object ExternalFragmentDomainFallback extends DomainParsingFallback {
  override def chooseFallback(element: ParsingInfo, availablePlugins: Seq[AMFParsePlugin]): BaseUnit = {
    val document = element.parsed
    ExternalFragment()
      .withId(document.location)
      .withLocation(document.location)
      .withEncodes(
        ExternalDomainElement()
          .withRaw(document.raw)
          .withMediaType(document.mediatype))
  }
}
