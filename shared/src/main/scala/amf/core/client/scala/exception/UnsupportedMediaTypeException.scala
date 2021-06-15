package amf.core.client.scala.exception

class UnsupportedMediaTypeException(val mime: String)
    extends RuntimeException(s"Cannot parse document with specified media type: $mime")
