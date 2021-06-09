package amf.core.internal.rdf.graph

import amf.core.client.scala.rdf.{PropertyObject, RdfModel, Uri}

class NodeFinder(graph: RdfModel) {
  def findLink(property: PropertyObject) = {
    property match {
      case Uri(v) => graph.findNode(v)
      case _      => None
    }
  }
}
