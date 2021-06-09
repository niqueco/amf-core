package amf.core.client.scala.model

trait AnyField extends BaseField with ValueField[Any] {

  /** Return value or null if value is null or undefined. */
  override def value(): Any = option().orNull
}
