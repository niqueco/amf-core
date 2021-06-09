package amf.core.internal.annotations

import amf.core.client.scala.model.domain.Annotation
import amf.core.client.scala.model.domain.extensions.DomainExtension

/** Amf annotation for custom domain properties (raml annotations). */
case class DomainExtensionAnnotation(extension: DomainExtension) extends Annotation
