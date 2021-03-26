package amf.core.exception

class InvalidDocumentHeaderException(vendor: String)
    extends RuntimeException(s"No valid header found in document for vendor: $vendor")
