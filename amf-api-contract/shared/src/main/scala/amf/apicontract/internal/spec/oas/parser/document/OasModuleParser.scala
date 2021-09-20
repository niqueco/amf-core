package amf.apicontract.internal.spec.oas.parser.document

import amf.apicontract.client.scala.model.document.APIContractProcessingData
import amf.apicontract.internal.spec.common.parser.{ReferencesParser, WebApiShapeParserContextAdapter}
import amf.apicontract.internal.spec.oas.parser.context.OasWebApiContext
import amf.core.client.scala.model.document.Module
import amf.core.client.scala.parse.document.SyamlParsedDocument
import amf.core.internal.annotations.SourceSpec
import amf.core.internal.metamodel.document.BaseUnitModel
import amf.core.internal.parser.{Root, YNodeLikeOps}
import amf.core.internal.parser.domain.Annotations
import org.yaml.model.YMap
import amf.core.internal.utils._

/**
  *
  */
case class OasModuleParser(root: Root)(implicit val ctx: OasWebApiContext)
    extends OasSpecParser()(WebApiShapeParserContextAdapter(ctx)) {

  def parseModule(): Module = {
    val sourceVendor = SourceSpec(ctx.spec)
    val module = Module(Annotations(root.parsed.asInstanceOf[SyamlParsedDocument].document))
      .withLocation(root.location)
      .withProcessingData(APIContractProcessingData())
      .adopted(root.location)
      .add(sourceVendor)
    module.set(BaseUnitModel.Location, root.location)

    root.parsed.asInstanceOf[SyamlParsedDocument].document.toOption[YMap].foreach { rootMap =>
      val references = ReferencesParser(module, root.location, "uses".asOasExtension, rootMap, root.references).parse()

      Oas2DocumentParser(root).parseDeclarations(root, rootMap)
      UsageParser(rootMap, module).parse()

      addDeclarationsToModel(module)
      if (references.nonEmpty) module.withReferences(references.baseUnitReferences())
    }

    module
  }
}
