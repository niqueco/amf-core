package amf.core.client.scala.model.domain

import amf.core.client.scala.model.domain.federation.HasShapeFederationMetadata
import amf.core.client.scala.model.domain.templates.Variable
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.domain._
import amf.core.internal.parser.domain.{Annotations, Fields}
import amf.core.internal.utils.AmfStrings

/** Base class for all dynamic DataNodes
  */
abstract class DataNode(annotations: Annotations)
    extends DomainElement
    with NamedDomainElement
    with HasShapeFederationMetadata {

  override protected def nameField: Field = DataNodeModel.Name // ??

  override def adopted(parent: String, cycle: Seq[String] = Seq()): this.type = {
    if (Option(id).isEmpty) simpleAdoption(parent) else this
  }

  override def componentId: String =
    "/" + name.option().getOrElse("data-node").urlComponentEncoded

  /** Replace all raml variables (any name inside double chevrons -> '<<>>') with the provided values. */
  def replaceVariables(values: Set[Variable], keys: Seq[ElementTree])(reportError: String => Unit): DataNode

  def forceAdopted(parent: String): this.type = {

    def isEnum(id: String) = id.split("/").dropRight(1).last == "in"
    // TODO: refactor this. Ids are okay in parsing stage but are lost on trait resolution in domain element merging.

    val adoptedId = parent + "/" + name
      .option()
      .map(_.urlComponentEncoded)
      .orNull
    val newId = Option(id) match {
      case Some(oldId: String) if oldId.endsWith("/included") =>
        adoptedId + "/included"
      case Some(oldId: String) if isEnum(oldId) =>
        adoptedId + "/enum"
      case _ => adoptedId
    }
    withId(newId)
  }

  override val fields: Fields = Fields()

  def copyNode(): DataNode
}
