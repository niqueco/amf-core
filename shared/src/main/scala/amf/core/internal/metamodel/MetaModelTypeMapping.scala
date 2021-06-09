package amf.core.internal.metamodel

import amf.core.client.scala.model.domain.{AmfObject, DomainElement}

trait MetaModelTypeMapping {

  /** Metadata Type references. */
  protected def metaModel(instance: Any): Obj = instance match {
    case obj: AmfObject => obj.meta
    case _              => throw new Exception(s"Missing metadata mapping for $instance")
  }

}
