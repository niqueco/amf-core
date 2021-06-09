package amf.core.client.scala.vocabulary

class UriType(id: String) extends ValueType(Namespace.Document, "") {
  override def iri(): String = id
}
