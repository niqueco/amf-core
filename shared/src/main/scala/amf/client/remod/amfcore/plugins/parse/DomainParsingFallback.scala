package amf.client.remod.amfcore.plugins.parse

import amf.core.exception.UnsupportedVendorException
import amf.core.model.document.{BaseUnit, ExternalFragment}
import amf.core.model.domain.ExternalDomainElement

trait DomainParsingFallback {

  def chooseFallback(element: ParsingInfo, availablePlugins: Seq[AMFParsePlugin]): BaseUnit
}

object ExternalFragmentDomainFallback extends DomainParsingFallback {
  override def chooseFallback(element: ParsingInfo, availablePlugins: Seq[AMFParsePlugin]): BaseUnit = {
    element.vendor match {
      case Some(definedVendor) =>
        throw new UnsupportedVendorException(definedVendor)
      case None =>
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
}
