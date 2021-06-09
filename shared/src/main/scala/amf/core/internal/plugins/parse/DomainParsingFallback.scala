package amf.core.internal.plugins.parse

import amf.core.client.scala.exception.UnsupportedVendorException
import amf.core.client.scala.model.document.{BaseUnit, ExternalFragment}
import amf.core.client.scala.model.domain.ExternalDomainElement
import amf.core.client.scala.parse.AMFParsePlugin
import amf.core.internal.parser.Root

trait DomainParsingFallback {

  def chooseFallback(root: Root, mediaType: Option[String], availablePlugins: Seq[AMFParsePlugin]): BaseUnit
}

object ExternalFragmentDomainFallback extends DomainParsingFallback {
  override def chooseFallback(root: Root, mediaType: Option[String], availablePlugins: Seq[AMFParsePlugin]): BaseUnit = {
    mediaType match {
      case Some(definedVendor) =>
        throw new UnsupportedVendorException(definedVendor)
      case None =>
        ExternalFragment()
          .withId(root.location)
          .withLocation(root.location)
          .withEncodes(
              ExternalDomainElement()
                .withRaw(root.raw)
                .withMediaType(root.mediatype))
    }

  }
}
