package amf.core.client.scala.model.domain

import amf.core.client.scala.model.StrField
import amf.core.client.scala.model.domain.common.DescribedElement
import amf.core.client.scala.model.domain.templates.Variable
import amf.core.client.scala.vocabulary.{Namespace, ValueType}
import amf.core.internal.metamodel.domain.LinkNodeModel
import amf.core.internal.parser.domain.{Annotations, Fields}

import scala.collection.mutable

/** Dynamic node representing a link to another dynamic node
  *
  * @param fields
  *   default fields for the dynamic node
  * @param annotations
  *   default annotations for the dynamic node
  */
class LinkNode(override val fields: Fields, val annotations: Annotations)
    extends DataNode(annotations)
    with DescribedElement {

  def link: StrField  = fields.field(LinkNodeModel.Value)
  def alias: StrField = fields.field(LinkNodeModel.Alias)

  def withLink(link: String): this.type   = set(LinkNodeModel.Value, link)
  def withAlias(alias: String): this.type = set(LinkNodeModel.Alias, alias)

  override def cloneElement(branch: mutable.Map[AmfObject, AmfObject]): LinkNode = {
    val node = super.cloneElement(branch).asInstanceOf[LinkNode]
    linkedDomainElement.foreach(node.withLinkedDomainElement)
    node
  }

  var linkedDomainElement: Option[DomainElement] = None

  override def replaceVariables(values: Set[Variable], keys: Seq[ElementTree])(reportError: String => Unit): DataNode =
    this

  def withLinkedDomainElement(domainElement: DomainElement): LinkNode = {
    linkedDomainElement = Some(domainElement)
    this
  }

  override def meta: LinkNodeModel.type = LinkNodeModel

  override def copyNode(): LinkNode = {
    val cloned = new LinkNode(fields.copy(), annotations.copy()).withId(id)

    cloned.linkedDomainElement = linkedDomainElement
    cloned
  }
}
object LinkNode {

  val builderType: ValueType = Namespace.Data + "Link"

  def apply(): LinkNode = apply(Annotations())

  def apply(annotations: Annotations): LinkNode = apply("", "", annotations)

  def apply(alias: String, value: String): LinkNode =
    apply(alias, value, Annotations())

  def apply(alias: String, value: String, annotations: Annotations): LinkNode = {
    val linkNode = new LinkNode(Fields(), annotations)
    linkNode.set(LinkNodeModel.Value, value)
    linkNode.set(LinkNodeModel.Alias, alias)
    linkNode
  }
}
