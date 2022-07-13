package amf.graphql.internal.spec.domain

import amf.graphql.internal.spec.context.GraphQLBaseWebApiContext
import amf.graphql.internal.spec.parser.syntax.TokenTypes._
import amf.shapes.client.scala.model.domain.NodeShape
import org.mulesoft.antlrast.ast.Node

case class GraphQLInputTypeParser(objTypeNode: Node)(implicit val ctx: GraphQLBaseWebApiContext)
    extends GraphQLCommonTypeParser {
  val obj: NodeShape = NodeShape(toAnnotations(objTypeNode))

  def parse(): NodeShape = {
    val (name, annotations) = findName(objTypeNode, "AnonymousInputType", "Missing name for input type")
    obj.withName(name, annotations).withIsInputOnly(true)
    collectFields()
    GraphQLDirectiveApplicationParser(objTypeNode, obj).parse()
    obj
  }

  def collectFields(): Unit = collectFieldsFromPath(objTypeNode, Seq(INPUT_FIELDS_DEFINITION, INPUT_VALUE_DEFINITION))

}
