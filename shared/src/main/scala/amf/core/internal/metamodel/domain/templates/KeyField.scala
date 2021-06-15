package amf.core.internal.metamodel.domain.templates

import amf.core.internal.metamodel.{Field, Obj}

/**
  * Determines a key field for merging.
  */
trait KeyField extends Obj {

  val key: Field

}
