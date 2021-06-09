package amf.core.client.scala.exception

class InvalidDocumentHeaderException(vendor: String)
    extends RuntimeException(s"No valid header found in document for vendor: $vendor")
