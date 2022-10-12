package amf.core.client.scala.model.document

import amf.core.internal.metamodel.{Field, Obj}
import amf.core.internal.metamodel.document.FragmentModel
import amf.core.internal.metamodel.domain.ModelDoc
import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.parser.domain.{Annotations, Fields}

case class RecursiveUnit(fields: Fields, annotations: Annotations) extends Fragment {
  override def meta: FragmentModel = new FragmentModel {
    override def fields: List[Field] = FragmentModel.fields

    override val `type`: List[ValueType] = FragmentModel.`type`
    override val doc: ModelDoc           = FragmentModel.doc

    override def modelInstance: AmfObject = RecursiveUnit()
  }

  override def componentId: String = "/recursive"
}

object RecursiveUnit {
  def apply(): RecursiveUnit = RecursiveUnit(Fields(), Annotations())
}
