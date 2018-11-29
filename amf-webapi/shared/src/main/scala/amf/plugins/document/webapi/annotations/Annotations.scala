package amf.plugins.document.webapi.annotations

import amf.core.annotations._
import amf.core.model.domain._
import amf.core.parser.Range
import amf.core.remote._
import amf.plugins.domain.shapes.annotations.ParsedFromTypeExpression
import amf.plugins.domain.webapi.annotations.ParentEndPoint
import amf.plugins.domain.webapi.models.EndPoint

case class ParsedJSONSchema(rawText: String) extends SerializableAnnotation with PerpetualAnnotation {
  override val name: String  = "parsed-json-schema"
  override val value: String = rawText
}

object ParsedJSONSchema extends AnnotationGraphLoader {
  override def unparse(annotatedValue: String, objects: Map[String, AmfElement]) = {
    ParsedJSONSchema(annotatedValue)
  }
}

case class ParsedJSONExample(rawText: String) extends SerializableAnnotation with PerpetualAnnotation {
  override val name: String  = "parsed-json-example"
  override val value: String = rawText
}

object ParsedJSONExample extends AnnotationGraphLoader {
  override def unparse(annotatedValue: String, objects: Map[String, AmfElement]) = {
    ParsedJSONExample(annotatedValue)
  }
}

case class SchemaIsJsonSchema() extends Annotation

case class GeneratedJSONSchema(rawText: String) extends Annotation

case class JSONSchemaId(id: String) extends SerializableAnnotation with PerpetualAnnotation {
  override val name: String  = "json-schema-id"
  override val value: String = id
}

object JSONSchemaId extends AnnotationGraphLoader {
  override def unparse(annotatedValue: String, objects: Map[String, AmfElement]) = {
    JSONSchemaId(annotatedValue)
  }
}

case class FormBodyParameter() extends SerializableAnnotation with PerpetualAnnotation {
  override val name: String  = "form-body-parameter"
  override val value: String = "true"
}

object FormBodyParameter extends AnnotationGraphLoader {
  override def unparse(annotatedValue: String, objects: Map[String, AmfElement]) = {
    FormBodyParameter()
  }
}

case class ParameterNameForPayload(paramName: String, range: Range)
    extends SerializableAnnotation
    with PerpetualAnnotation { // perpetual? after resolution i should have a normal payload
  override val name: String  = "parameter-name-for-payload"
  override val value: String = paramName + "->" + range.toString
}

object ParameterNameForPayload extends AnnotationGraphLoader {
  override def unparse(annotatedValue: String, objects: Map[String, AmfElement]) = {
    annotatedValue.split("->") match {
      case Array(req, range) =>
        new ParameterNameForPayload(req, Range.apply(range))
    }

  }
}

case class RequiredParamPayload(required: Boolean, range: Range)
    extends SerializableAnnotation
    with PerpetualAnnotation { // perpetual? after resolution i should have a normal payload
  override val name: String  = "required-param-payload"
  override val value: String = required + "->" + range.toString
}

object RequiredParamPayload extends AnnotationGraphLoader {
  override def unparse(annotatedValue: String, objects: Map[String, AmfElement]) = {
    annotatedValue.split("->") match {
      case Array(req, range) =>
        val required = if (req.equals("true")) true else false
        new RequiredParamPayload(required, Range.apply(range))
    }
  }
}

case class LocalLinkPath(rawPath: String) extends SerializableAnnotation {
  override val name: String  = "local-link-path"
  override val value: String = rawPath
}

object LocalLinkPath extends AnnotationGraphLoader {
  override def unparse(annotatedValue: String, objects: Map[String, AmfElement]) = {
    LocalLinkPath(annotatedValue)
  }
}

case class InlineDefinition() extends Annotation

/*
case class DomainElementReference(name: String, ref: Option[DomainEntity]) extends SerializableAnnotation {
  override val value: String = name
}

object DomainElementReference extends AnnotationGraphLoader {
  override def unparse(annotatedValue: String, objects: Map[String, AmfElement]) = ???
}
 */

case class EndPointBodyParameter() extends Annotation

case class DefaultPayload() extends Annotation

case class EndPointParameter() extends Annotation

// save original text link?
case class ReferencedElement(parsedUrl: String, referenced: DomainElement) extends Annotation

case class Inferred() extends Annotation

case class CollectionFormatFromItems() extends Annotation

object WebApiAnnotations {

  private def sourceVendor(value: String, objects: Map[String, AmfElement]) = {
    value match {
      case Vendor(vendor) => SourceVendor(vendor)
      case _              => SourceVendor(Vendor(value))
    }
  }

  private def parentEndPoint(value: String, objects: Map[String, AmfElement]) = {
    ParentEndPoint(objects(value).asInstanceOf[EndPoint])
  }

  private def singleValueArray(value: String, objects: Map[String, AmfElement]) = {
    SingleValueArray()
  }

  private def aliases(value: String, objects: Map[String, AmfElement]) = {
    Aliases.unparse(value, objects)
  }

  private def parsedJsonSchema(value: String, objects: Map[String, AmfElement]) = {
    ParsedJSONSchema(value)
  }

  private def declaredElement(value: String, objects: Map[String, AmfElement]) = {
    DeclaredElement()
  }

  private def typeExpression(value: String, objects: Map[String, AmfElement]) = {
    ParsedFromTypeExpression(value)
  }

  private def synthesizedField(value: String, objects: Map[String, AmfElement]) = {
    SynthesizedField()
  }

  private def lexical(value: String, objects: Map[String, AmfElement]) = {
    LexicalInformation(Range.apply(value))
  }
}