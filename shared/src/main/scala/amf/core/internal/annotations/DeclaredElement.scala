package amf.core.internal.annotations

import amf.core.internal.metamodel.domain.DomainElementModel
import amf.core.client.scala.model.domain._

case class TypeAlias(aliasId: String) extends Annotation

case class DeclaredElement() extends EternalSerializedAnnotation {
  override val name: String = DeclaredElement.name

  override val value: String = ""
}

object DeclaredElement extends AnnotationGraphLoader {
  override def unparse(value: String, objects: Map[String, AmfElement]): Option[Annotation] = Some(DeclaredElement())

  def name: String = "declared-element"
}

trait ErrorDeclaration[M <: DomainElementModel] extends DomainElement {
  val namespace: String
  val model: M

  override def withId(value: String): ErrorDeclaration.this.type = super.withId(namespace + value)

  // APIMF-2976: we need to generated new models for error declarations
  override def meta: M = model

  override private[amf] def newInstance(): ErrorDeclaration[M] = newErrorInstance

  protected def newErrorInstance: ErrorDeclaration[M]
}
