package amf.core.internal.plugins.document.graph.parser
import amf.core.client.scala.model.document.{BaseUnit, SourceMap}
import amf.core.client.scala.model.domain.AmfElement
import amf.core.client.scala.parse.document.SyamlParsedDocument
import amf.core.internal.parser.{CompilerConfiguration, ParseConfiguration}
import amf.core.internal.parser.domain.Annotations
import org.yaml.model.YDocument

abstract class GraphParser(parserConfig: ParseConfiguration) extends GraphParserHelpers {
  def canParse(document: SyamlParsedDocument): Boolean
  def parse(document: YDocument, location: String): BaseUnit

  def annotations(nodes: Map[String, AmfElement], sources: SourceMap, key: String): Annotations =
    parserConfig.serializableAnnotationsFacade.retrieveAnnotation(nodes, sources, key)
}
