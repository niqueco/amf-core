package amf.core.client.scala.exception

class UnsupportedVendorException(val vendor: String)
    extends RuntimeException(s"Cannot parse document with specified vendor: $vendor")
