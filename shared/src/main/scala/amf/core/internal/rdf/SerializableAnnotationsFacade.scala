package amf.core.internal.rdf

import amf.core.client.scala.model.document.SourceMap
import amf.core.client.scala.model.domain.{AmfElement, AnnotationGraphLoader}
import amf.core.internal.parser.ParseConfiguration
import amf.core.internal.parser.domain.Annotations
import org.mulesoft.common.core.CachedFunction
import org.mulesoft.common.functional.MonadInstances._

import scala.collection.immutable.ListMap
import scala.collection.mutable

class SerializableAnnotationsFacade private[amf] (parserConfig: ParseConfiguration) {

  def retrieveAnnotation(nodes: Map[String, AmfElement], sources: SourceMap, key: String): Annotations = {
    val annotations =
      sources.annotations.to(ListMap).flatMap { case (annotation, values: mutable.Map[String, String]) =>
        values.get(key) match {
          case Some(value) => findAnnotation(annotation).flatMap(_.unparse(value, nodes))
          case _           => None
        }
      }
    Annotations(annotations.toList)
  }

  private val findAnnotation = CachedFunction.fromMonadic(parserConfig.registryContext.findAnnotation)

  private def findAnnotation(annotationID: String): Option[AnnotationGraphLoader] =
    findAnnotation.runCached(annotationID)

}
