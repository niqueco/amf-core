package amf.core.client.scala.exception

class InvalidDocumentHeaderException(spec: String)
    extends RuntimeException(s"No valid header found in document for spec: $spec")
