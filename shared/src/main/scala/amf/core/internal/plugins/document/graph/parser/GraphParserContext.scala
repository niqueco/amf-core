package amf.core.internal.plugins.document.graph.parser

import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.parse.document.{EmptyFutureDeclarations, ParsedReference, ParserContext}
import amf.core.internal.parser.{CompilerConfiguration, LimitedParseConfig, ParseConfiguration}
import amf.core.internal.parser.domain.FutureDeclarations
import amf.core.internal.plugins.document.graph.context.GraphContext

class GraphParserContext(rootContextDocument: String = "",
                         refs: Seq[ParsedReference] = Seq.empty,
                         futureDeclarations: FutureDeclarations = EmptyFutureDeclarations(),
                         config: ParseConfiguration,
                         val graphContext: GraphContext = GraphContext())
    extends ParserContext(rootContextDocument, refs, futureDeclarations, config) {}
