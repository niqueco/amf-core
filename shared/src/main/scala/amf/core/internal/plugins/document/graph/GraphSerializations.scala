package amf.core.internal.plugins.document.graph

trait GraphSerialization

/**
  * JSON-LD serializations
  */
trait JsonLdDocumentForm {
  def name: String
  def extension: String
}

object NoForm extends JsonLdDocumentForm {
  override def extension: String = "jsonld"

  override def name: String = "No form"
}

object FlattenedForm extends JsonLdDocumentForm {
  override def extension: String = "flattened.jsonld"

  override def name: String = "Flattened form"
}

object EmbeddedForm extends JsonLdDocumentForm {
  override def extension: String = "expanded.jsonld" // Legacy extension

  override def name: String = "Embedded form"
}

case class JsonLdSerialization(form: JsonLdDocumentForm) extends GraphSerialization

/**
  * RDF serializations
  */
case class RdfSerialization() extends GraphSerialization
