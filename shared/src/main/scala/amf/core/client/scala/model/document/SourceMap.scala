package amf.core.client.scala.model.document

import amf.core.client.scala.model.domain.AmfElement
import amf.core.internal.parser.domain.Value

import scala.collection.mutable

/** Source maps for graph: Map(annotation -> Map(element -> value))
  */
class SourceMap(
    val annotations: mutable.Map[String, mutable.Map[String, String]],
    val eternals: mutable.Map[String, mutable.Map[String, String]]
) {

  def annotation(annotation: String): (String, String) => Unit = {
    val map = annotations.get(annotation).orElse(eternals.get(annotation)) match {
      case Some(values) => values
      case None =>
        val values = mutable.Map[String, String]()
        annotations += (annotation -> values)
        values
    }
    map.update
  }

  def property(element: String)(value: Value): Unit = {
    value.annotations
      .serializables()
      .foreach(a => {
        val tuple = element -> a.value
        annotations.get(a.name) match {
          case Some(values) => values += tuple
          case None         => annotations += (a.name -> mutable.Map(tuple))
        }
      })
    value.annotations
      .eternals()
      .foreach(e => {
        val tuple = element -> e.value
        eternals.get(e.name) match {
          case Some(values) => values += tuple
          case None         => eternals += (e.name -> mutable.Map(tuple))
        }
      })
  }

  def all(): mutable.Map[String, mutable.Map[String, String]] =
    (annotations ++ eternals).asInstanceOf[mutable.Map[String, mutable.Map[String, String]]]

  def nonEmpty: Boolean = annotations.nonEmpty

  def isEmpty: Boolean = annotations.isEmpty

  def serializablesNonEmpty: Boolean = annotations.nonEmpty
}

object SourceMap {
  def apply(): SourceMap = new SourceMap(mutable.Map(), mutable.Map())

  def apply(id: String, element: AmfElement): SourceMap = {
    val map = SourceMap()
    element.annotations
      .serializables()
      .foreach(a => {
        map.annotations += (a.name -> mutable.Map(id -> a.value))
      })

    element.annotations
      .eternals()
      .foreach(e => {
        map.eternals += (e.name -> mutable.Map(id -> e.value))
      })
    map
  }

  val empty: SourceMap = new SourceMap(mutable.Map.empty, mutable.Map.empty)
}
