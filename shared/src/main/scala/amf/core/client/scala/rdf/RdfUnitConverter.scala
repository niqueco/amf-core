package amf.core.client.scala.rdf

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.rdf.RdfModelParser

class RdfUnitConverter {

  def fromNativeRdfModel(id: String, rdfModel: RdfModel, conf: AMFGraphConfiguration): BaseUnit = {
    RdfModelParser(conf).parse(rdfModel, id)
  }

}
