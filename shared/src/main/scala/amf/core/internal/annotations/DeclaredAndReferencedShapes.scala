package amf.core.internal.annotations

import amf.core.client.scala.model.domain.{Annotation, DomainElement}

/**
  * Used to maintain shapes that where declarations or references, used for optimizing emission.
  * These shapes may latter be extracted to declares array in jsonld and avoid multiple emissions.
  * These annotations must not be serialized into jsonld.
  */
case class References(references: Seq[String]) extends Annotation
case class Declares(declares: Seq[String]) extends Annotation
