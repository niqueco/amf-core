package amf.core.client.scala.model

trait IntField extends ValueField[Int] {

  /** Return int value or `0` if value is null or undefined. */
  override def value(): Int = option() match {
    case Some(v) => v
    case _       => 0
  }
}
