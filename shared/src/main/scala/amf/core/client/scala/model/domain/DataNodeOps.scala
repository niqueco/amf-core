package amf.core.client.scala.model.domain

object DataNodeOps {

  /** Adopt entire data node hierarchy. */
  def adoptTree(id: String, node: DataNode): DataNode = {
    node.forceAdopted(id)
    node match {
      case array: ArrayNode =>
        array.members.foreach(adoptTree(array.id, _))
      case obj: ObjectNode =>
        obj
          .propertyFields()
          .map(obj.fields.field[DataNode])
          .foreach(e => adoptTree(obj.id, e))
      case _ =>
    }
    node
  }
}

case class ElementTree(key: String, subtrees: Seq[ElementTree])
