package amf.core.client.scala.model

trait AnyField extends ValueField[Any] {

  /** Return value or null if value is null or undefined. */
  override def value(): Any = option().orNull
}
