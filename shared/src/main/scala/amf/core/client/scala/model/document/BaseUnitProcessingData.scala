package amf.core.client.scala.model.document

import amf.core.client.scala.model.{BoolField, StrField}
import amf.core.client.scala.model.domain.{AmfObject, DomainElement}
import amf.core.internal.metamodel.document.BaseUnitProcessingDataModel
import amf.core.internal.metamodel.document.BaseUnitProcessingDataModel._
import amf.core.internal.parser.domain.{Annotations, Fields}
import amf.core.internal.remote.Spec

class BaseUnitProcessingData(val fields: Fields, val annotations: Annotations) extends AmfObject {

  /** Value , path + field value that is used to compose the id when the object its adopted */
  override def componentId = "/BaseUnitProcessingData"

  def transformed: BoolField = fields.field(Transformed)

  def withTransformed(value: Boolean): this.type = set(Transformed, value)

  def sourceSpec: StrField = fields.field(SourceSpec)

  def withSourceSpec(spec: String): this.type = set(SourceSpec, Spec(spec).id)
  def withSourceSpec(spec: Spec): this.type   = set(SourceSpec, spec.id)

  override def meta: BaseUnitProcessingDataModel = BaseUnitProcessingDataModel

  protected[amf] def sourceSpecProvider: Option[Spec] = sourceSpec.option().map(x => Spec(x))
}

object BaseUnitProcessingData {
  def apply(): BaseUnitProcessingData = apply(Annotations())

  def apply(annotations: Annotations): BaseUnitProcessingData = new BaseUnitProcessingData(Fields(), annotations)
}
