package amf.core.internal.parser

import amf.core.client.scala.parse.document.{ParsedDocument, ParsedReference, ReferenceKind}
import amf.core.internal.utils.AmfStrings

case class Root(parsed: ParsedDocument,
                location: String,
                mediatype: String,
                references: Seq[ParsedReference],
                referenceKind: ReferenceKind,
                raw: String)
