package amf.core.client.scala.model.domain

import amf.core.client.scala.model.domain.common.HasDescription
import amf.core.client.scala.model.domain.templates.Variable
import amf.core.client.scala.vocabulary.{Namespace, ValueType}
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.EncodedIri
import amf.core.internal.metamodel.domain.{ArrayNodeModel, ModelDoc, ModelVocabularies}
import amf.core.internal.parser.domain.{Annotations, Fields}
import org.yaml.model.YSequence

/** Arrays of values
  */
class ArrayNode(override val fields: Fields, val annotations: Annotations) extends DataNode(annotations) with HasDescription {

  def members: Seq[DataNode] = fields.field(ArrayNodeModel.Member)

  def addMember(member: DataNode): Seq[DataNode] = {
    val newArray = members :+ member
    set(ArrayNodeModel.Member, AmfArray(newArray))
    newArray
  }

  def withMembers(members: Seq[DataNode]): this.type = {
    set(ArrayNodeModel.Member, AmfArray(members))
    this
  }

  override def replaceVariables(values: Set[Variable], keys: Seq[ElementTree])(
      reportError: String => Unit
  ): DataNode = {
    val newMembers = members.map(_.replaceVariables(values, keys)(reportError))
    withMembers(newMembers)
  }

  def positionFields(): Seq[Field] = members.zipWithIndex.map { case (_, i) =>
    Field(EncodedIri, Namespace.Data + s"pos$i", ModelDoc(ModelVocabularies.Data, s"pos$i"))
  }

  override def meta: ArrayNodeModel.type = ArrayNodeModel

  override def copyNode(): this.type = {
    val cloned =
      new ArrayNode(fields.copy().filter(e => e._1 != ArrayNodeModel.Member), annotations.copy()).withId(id)

    if (id != null) cloned.withId(id)

    cloned.withMembers(members.map(_.copyNode()))

    cloned.asInstanceOf[this.type]
  }
}

object ArrayNode {

  val builderType: ValueType = Namespace.Data + "Array"

  def apply(): ArrayNode = apply(Annotations())

  def apply(ast: YSequence): ArrayNode = apply(Annotations(ast))

  def apply(annotations: Annotations): ArrayNode =
    new ArrayNode(Fields(), annotations)
}
