package amf.core.internal.plugins.document.graph.parser

import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.parse.document.ParserContext
import amf.core.client.scala.vocabulary.{AbsoluteIri, IriClassification, RelativeIri}
import amf.core.internal.metamodel.Obj
import amf.core.internal.parser.YMapOps
import amf.core.internal.plugins.document.graph.JsonLdKeywords
import amf.core.internal.plugins.document.graph.context.GraphContextOperations
import amf.core.internal.validation.CoreValidations.MissingIdInNode
import org.yaml.model.{YMap, YNode, YScalar, YSequence}

abstract class GraphContextHelper extends GraphContextOperations {

  protected def expandUriFromContext(iri: String)(implicit ctx: GraphParserContext): String = {
    expand(iri)(ctx.graphContext)
  }

  protected def compactUriFromContext(iri: String)(implicit ctx: GraphParserContext): String = {
    compact(iri)(ctx.graphContext)
  }

  protected def transformIdFromContext(iri: String)(implicit ctx: GraphParserContext): String = {
    IriClassification.classify(iri) match {
      case AbsoluteIri => iri
      case RelativeIri => resolveWithBase(iri, ctx)
    }
  }

  protected def adaptUriToContext(iri: String)(implicit ctx: GraphParserContext): String = {
    IriClassification.classify(iri) match {
      case AbsoluteIri => iri.stripPrefix(getPrefixOption(iri, ctx).getOrElse(""))
      case RelativeIri => iri
    }
  }

  private def getPrefixOption(id: String, ctx: GraphParserContext): Option[String] = {
    ctx.graphContext.base.map { base =>
      if (id.startsWith("./")) base.parent.iri + "/"
      else base.iri
    }
  }

  private def resolveWithBase(id: String, ctx: GraphParserContext): String = {
    val prefixOption = getPrefixOption(id, ctx)
    val prefix       = prefixOption.getOrElse("")
    s"$prefix$id"
  }

  protected def nodeIsOfType(node: YNode, obj: Obj)(implicit ctx: GraphParserContext): Boolean = {
    node.value match {
      case map: YMap => nodeIsOfType(map, obj)
      case _         => false
    }
  }

  protected def nodeIsOfType(map: YMap, obj: Obj)(implicit ctx: GraphParserContext): Boolean = {
    map.key(JsonLdKeywords.Type).exists { entry =>
      val types = entry.value.as[YSequence].nodes.flatMap(_.asScalar)
      types.exists(`type` => {
        val typeIri = expandUriFromContext(`type`.text)
        obj.`type`.map(_.iri()).contains(typeIri)
      })
    }
  }

  protected def retrieveId(map: YMap, ctx: ParserContext): Option[String] = {
    implicit val errorHandler: AMFErrorHandler = ctx.eh

    map.key(JsonLdKeywords.Id) match {
      case Some(entry) => Some(entry.value.as[YScalar].text)
      case _ =>
        ctx.eh.violation(MissingIdInNode, "", s"No @id declaration on node $map", map.location)
        None
    }
  }
}