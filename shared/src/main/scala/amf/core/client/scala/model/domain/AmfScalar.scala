package amf.core.client.scala.model.domain

import amf.core.internal.parser.domain.Annotations

import scala.collection.mutable

/** Amf Scalar
  */
case class AmfScalar(value: Any, annotations: Annotations = new Annotations()) extends AmfElement {

  override def toString: String = {
    Option(value) match {
      case Some(v) => v.toString
      case None    => ""
    }
  }

  def toBool: Boolean = {
    Option(value) match {
      case Some(v: String) => v.toBoolean
      case Some(true)      => true
      case Some(false)     => false
      case _               => throw new Exception("Cannot transform scalar value into Boolean")
    }
  }

  def toNumber: Number = {
    toNumberOption match {
      case Some(v: Number) => v
      case None            => throw new Exception("Cannot transform null value into Number")
    }
  }

  def toNumberOption: Option[Number] = {
    Option(value).map {
      case v: String => v.toDouble
      case v         => v.asInstanceOf[Number]
    }
  }

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case AmfScalar(otherValue, _) =>
        value == (otherValue)
      case _ => false
    }
  }

  override def cloneElement(branch: mutable.Map[AmfObject, AmfObject]): AmfScalar =
    AmfScalar(value, annotations.copy())
}
