package amf.core.internal.utils
import amf.core.client.scala.model.StrField

import scala.language.implicitConversions

// Eq type class for type safe equality comparison
trait Eq[T] {
  def eqv(a: T, b: T): Boolean
}

object EqInstances {
  implicit val stringEq: Eq[String] = (a: String, b: String) => a == b
  implicit def optionEq[T](implicit eq: Eq[T]): Eq[Option[T]] = (a: Option[T], b: Option[T]) => {
    (a, b) match {
      case (Some(x), Some(y)) => eq.eqv(x, y)
      case (None, None)       => true
      case _                  => false
    }
  }
  implicit def strFieldEq(implicit eq: Eq[Option[String]]): Eq[StrField] = (a: StrField, b: StrField) => {
    eq.eqv(a.option(), b.option())
  }
}

// Pretty syntax

object EqSyntax {
  implicit def toOps[T](lhs: T): EqOps[T] = EqOps(lhs)
}

final case class EqOps[T](lhs: T) {
  def ===(rhs: T)(implicit eq: Eq[T]): Boolean = eq.eqv(lhs, rhs)
  def =!=(rhs: T)(implicit eq: Eq[T]): Boolean = !eq.eqv(lhs, rhs)
}
