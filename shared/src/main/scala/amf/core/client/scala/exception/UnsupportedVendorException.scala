package amf.core.client.scala.exception

class UnsupportedVendorException(val spec: String)
    extends RuntimeException(s"Cannot parse document with specified spec: $spec")
