package amf.core.client.scala.model.document

import amf.core.internal.annotations.SourceLocation
import amf.core.internal.metamodel.document.{FragmentModel, PayloadFragmentModel}
import amf.core.client.scala.model.StrField
import amf.core.client.scala.model.domain.{DataNode, ScalarNode}
import amf.core.internal.parser.domain.Fields
import amf.core.internal.parser.domain.{Annotations, Fields}

case class PayloadFragment(fields: Fields = Fields(), annotations: Annotations = Annotations()) extends Fragment {
  override def encodes: DataNode = fields(PayloadFragmentModel.Encodes)

  /** Meta data for the document */
  override def meta: PayloadFragmentModel.type = PayloadFragmentModel

  def withMediaType(mediaType: String): this.type = {
    set(PayloadFragmentModel.MediaType, mediaType)
    this
  }

  def mediaType: StrField = fields.field(PayloadFragmentModel.MediaType)
}

object PayloadFragment {
  private def apply(payload: DataNode): PayloadFragment = {
    val fragment = apply().set(FragmentModel.Encodes, payload, Annotations.inferred())
    payload.annotations.find(classOf[SourceLocation]).foreach(l => fragment.withLocation(l.location))
    fragment
  }

  private def apply(): PayloadFragment =
    PayloadFragment(Fields(), Annotations())
      .withId("http://test.com/payload")

  def apply(payload: String, mediaType: String): PayloadFragment = apply(ScalarNode(payload, None), mediaType)

  def apply(payload: DataNode, mediaType: String): PayloadFragment =
    apply(payload)
      .withMediaType(mediaType)
}
