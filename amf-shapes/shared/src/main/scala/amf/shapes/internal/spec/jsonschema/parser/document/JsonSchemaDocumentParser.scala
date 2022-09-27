package amf.shapes.internal.spec.jsonschema.parser.document

import amf.aml.internal.parse.common.DeclarationKeyCollector
import amf.core.client.scala.model.document.BaseUnitProcessingData
import amf.core.client.scala.model.domain.AmfScalar
import amf.core.client.scala.parse.document.SyamlParsedDocument
import amf.core.internal.metamodel.document.{BaseUnitModel, DocumentModel}
import amf.core.internal.parser.domain.Annotations
import amf.core.internal.parser.{Root, YMapOps, YNodeLikeOps}
import amf.core.internal.remote.Spec
import amf.core.internal.utils.AmfStrings
import amf.shapes.client.scala.model.document.JsonSchemaDocument
import amf.shapes.client.scala.model.domain.AnyShape
import amf.shapes.internal.document.metamodel.JsonSchemaDocumentModel
import amf.shapes.internal.spec.common.parser.TypeDeclarationParser.parseTypeDeclarations
import amf.shapes.internal.spec.common.parser.{
  BaseReferencesParser,
  QuickFieldParserOps,
  ShapeParserContext,
  YMapEntryLike
}
import amf.shapes.internal.spec.common.{
  JSONSchemaDraft201909SchemaVersion,
  JSONSchemaUnspecifiedVersion,
  JSONSchemaVersion,
  SchemaVersion
}
import amf.shapes.internal.spec.jsonschema.JsonSchemaEntry
import amf.shapes.internal.spec.oas.parser.OasTypeParser
import amf.shapes.internal.validation.definitions.ShapeParserSideValidations.{MandatorySchema, UnknownSchemaDraft}
import org.yaml.model.{YMap, YMapEntry, YScalar}

case class JsonSchemaDocumentParser(root: Root)(implicit val ctx: ShapeParserContext)
    extends DeclarationKeyCollector
    with QuickFieldParserOps {

  val map: YMap = root.parsed.asInstanceOf[SyamlParsedDocument].document.as[YMap]

  def parse(): JsonSchemaDocument = {

    val document = JsonSchemaDocument(Annotations(root.parsed.asInstanceOf[SyamlParsedDocument].document))
      .withLocation(root.location)
      .withProcessingData(BaseUnitProcessingData().withSourceSpec(Spec.JSONSCHEMA))

    document.set(BaseUnitModel.Location, root.location)

    val (schemaVersion, _) = setSchemaVersion(document)

    root.parsed.asInstanceOf[SyamlParsedDocument].document.toOption[YMap].foreach { rootMap =>
      val references =
        BaseReferencesParser(document, root.location, "uses".asOasExtension, rootMap, root.references).parse()

      // Parsing declaration schemas from "definitions" or "$defs"
      parseTypeDeclarations(
        map,
        declarationsKey(schemaVersion),
        Some(this),
        Some(document),
        Option(schemaVersion)
      )
      addDeclarationsToModel(document, ctx.shapes.values.toList)

      if (references.nonEmpty) document.withReferences(references.baseUnitReferences())

      val rootSchema = parseRootSchema(schemaVersion)
      document.set(DocumentModel.Encodes, rootSchema, Annotations.inferred())

      ctx.futureDeclarations.resolve()
    }

    document
  }

  private def parseRootSchema(schemaVersion: JSONSchemaVersion, name: String = "schema"): AnyShape = {
    OasTypeParser(YMapEntryLike(map), name, _ => {}, schemaVersion)
      .parse()
      .getOrElse(AnyShape().withName(name))
  }

  private def setSchemaVersion(document: JsonSchemaDocument): (JSONSchemaVersion, JsonSchemaDocument) = {
    val schemaEntry: Option[YMapEntry]   = map.key("$schema")
    val schemaVersion: JSONSchemaVersion = processSchemaEntry(document, schemaEntry)

    schemaEntry.map { entry =>
      document.set(
        JsonSchemaDocumentModel.SchemaVersion,
        AmfScalar(schemaVersion.url, Annotations(entry.value)),
        Annotations(entry)
      )
    }

    (schemaVersion, document)
  }

  private def processSchemaEntry(document: JsonSchemaDocument, schemaEntry: Option[YMapEntry]): JSONSchemaVersion = {
    schemaEntry match {
      case Some(entry) => computeSchemaVersion(document, entry)
      case None =>
        ctx.eh.violation(MandatorySchema, document, MandatorySchema.message)
        JSONSchemaUnspecifiedVersion
    }
  }

  private def computeSchemaVersion(document: JsonSchemaDocument, schemaEntry: YMapEntry) = {
    JsonSchemaEntry(schemaEntry.value.as[YScalar].text) match {
      case Some(version) => version
      case None =>
        ctx.eh.violation(UnknownSchemaDraft, document, UnknownSchemaDraft.message)
        JSONSchemaUnspecifiedVersion
    }
  }

  private def declarationsKey(version: JSONSchemaVersion): Seq[String] = version match {
    case JSONSchemaDraft201909SchemaVersion => Seq($defKey, definitionsKey)
    case _                                  => Seq(definitionsKey)
  }

  final val definitionsKey  = "definitions"
  final val $defKey: String = "$defs"
}
