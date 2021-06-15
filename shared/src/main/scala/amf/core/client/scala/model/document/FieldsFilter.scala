package amf.core.client.scala.model.document
import amf.core.internal.metamodel.document.DocumentModel.References
import amf.core.client.scala.model.domain.AmfElement
import amf.core.internal.parser.domain.Fields

trait FieldsFilter {
  def filter(fields: Fields): List[AmfElement]
}

object FieldsFilter {

  /** Scope does not include external references. */
  object Local extends FieldsFilter {
    override def filter(fields: Fields): List[AmfElement] =
      fields
        .fields()
        .collect {
          case e if e.field != References => e.element
        }
        .toList
  }

  /** Scope includes external references. */
  object All extends FieldsFilter {
    override def filter(fields: Fields): List[AmfElement] =
      fields.fields().map(_.element).toList
  }

}
