package amf.core.client.scala.model.document

import amf.core.internal.metamodel.document.ExternalFragmentModel
import amf.core.client.scala.model.domain.ExternalDomainElement
import amf.core.internal.parser.domain.{Annotations, Fields}

case class ExternalFragment(fields: Fields, annotations: Annotations) extends Fragment {
  override def encodes: ExternalDomainElement = super.encodes.asInstanceOf[ExternalDomainElement]

  /** Meta data for the document */
  override def meta: ExternalFragmentModel.type = ExternalFragmentModel
}

object ExternalFragment {
  def apply(): ExternalFragment                         = apply(Annotations())
  def apply(annotations: Annotations): ExternalFragment = ExternalFragment(Fields(), annotations)
}
