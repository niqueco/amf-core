package amf.core.client.scala.model.domain

import amf.core.client.scala.model.domain.common.DescribedElement
import amf.core.client.scala.model.domain.templates.Variable
import amf.core.client.scala.vocabulary.Namespace.Data
import amf.core.client.scala.vocabulary.{Namespace, ValueType}
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.domain.{DataNodeModel, ModelDoc, ModelVocabularies, ObjectNodeModel}
import amf.core.internal.parser.domain.{Annotations, FieldEntry, Fields, Value}
import amf.core.internal.transform.VariableReplacer
import amf.core.internal.utils.AmfStrings
import org.yaml.model.YPart

/** Data records, with a list of properties
  */
class ObjectNode(override val fields: Fields, val annotations: Annotations)
    extends DataNode(annotations)
    with DescribedElement {

  def getFromKey(key: String): Option[DataNode] =
    fields
      .getValueAsOption(createField(key))
      .collectFirst({ case Value(e: DataNode, _) => e })

  def addProperty(propertyOrUri: String, objectValue: DataNode, annotations: Annotations = Annotations()): this.type = {
    val property = ensurePlainProperty(propertyOrUri)
    overrideName(objectValue, property)
    addPropertyByField(createField(property), objectValue, annotations)
    this
  }

  private def overrideName(objectValue: DataNode, newName: String) = {
    objectValue.fields
      .getValueAsOption(DataNodeModel.Name)
      .fold({
        objectValue.withSynthesizeName(newName)
      })(value => {
        objectValue.set(DataNodeModel.Name, AmfScalar(newName, value.value.annotations), value.annotations)
      })
  }

  private def createField(property: String) = {
    Field(DataNodeModel, Namespace.Data + property.urlComponentEncoded, ModelDoc(ModelVocabularies.Data, property))
  }

  def addPropertyByField(f: Field, objectValue: DataNode, annotations: Annotations = Annotations()): this.type = {
    objectValue.adopted(this.id)
    set(f, objectValue, annotations)
    this
  }

  def propertyFields(): Iterable[Field] =
    fields
      .fields()
      .flatMap({
        case FieldEntry(field, _) if !ObjectNodeModel.fields.contains(field) =>
          Some(field)
        case _ => None
      })

  def allProperties(): Iterable[DataNode] = allPropertiesWithName().values

  def allPropertiesWithName(): Map[String, DataNode] =
    propertyFields().map(f => f.value.name.urlComponentDecoded -> fields[DataNode](f)).toMap

  protected def ensurePlainProperty(propertyOrUri: String): String =
    if (propertyOrUri.indexOf(Namespace.Data.base) == 0) {
      propertyOrUri.replace(Namespace.Data.base, "")
    } else {
      propertyOrUri
    }

  override val meta: ObjectNodeDynamicModel = new ObjectNodeDynamicModel()

  override def replaceVariables(values: Set[Variable], keys: Seq[ElementTree])(
      reportError: String => Unit
  ): DataNode = {

    propertyFields().foreach { field =>
      val decodedKey = field.value.name.urlComponentDecoded
      val finalKey: String =
        if (decodedKey.endsWith("?"))
          decodedKey.substring(0, decodedKey.length - 1)
        else decodedKey
      val maybeTree = keys.find(_.key.equals(finalKey))

      fields.entry(field) match {
        case Some(FieldEntry(_, v)) =>
          val value = v.value
            .asInstanceOf[DataNode]
            .replaceVariables(values, maybeTree.map(_.subtrees).getOrElse(Nil))(
                if (
                    decodedKey
                      .endsWith("?") && maybeTree.isEmpty
                ) // TODO review this logic
                  (_: String) => Unit
                else reportError
            ) // if its an optional node, ignore the violation of the var not implement
          fields.removeField(field)
          addProperty(VariableReplacer.replaceVariablesInKey(decodedKey, values, reportError), value, v.annotations)
        case _ =>
      }
    }

    this
  }

  class ObjectNodeDynamicModel extends ObjectNodeModel {
    override val `type`: List[ValueType] = Data + "Object" :: DataNodeModel.`type`

    override def fields: List[Field] =
      propertyFields().toList ++ DataNodeModel.fields

    override def modelInstance: ObjectNode = ObjectNode()

    override val doc: ModelDoc = ModelDoc(
        ModelVocabularies.Data,
        "ObjectNode",
        "Node that represents a dynamic object with records data structure"
    )
  }

  override def copyNode(): ObjectNode = {

    val cloned = new ObjectNode(fields.copy(), annotations.copy())

    if (id != null) cloned.withId(id)

    propertyFields().flatMap(f => fields.entry(f)).foreach { entry =>
      val value = entry.value
      cloned.set(entry.field, value.value.asInstanceOf[DataNode].copyNode(), value.annotations)
    }

    cloned
  }
}

object ObjectNode {

  val builderType: ValueType = Namespace.Data + "Object"

  def apply(): ObjectNode = apply(Annotations())

  def apply(ast: YPart): ObjectNode = apply(Annotations(ast))

  def apply(annotations: Annotations): ObjectNode =
    new ObjectNode(Fields(), annotations)

}
