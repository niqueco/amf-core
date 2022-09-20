package amf.core.internal.plugins.document.graph.emitter

import amf.core.client.scala.config.RenderOptions
import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.metamodel.{Field, Obj}

class SemanticExtensionAwareFieldRenderProvision(
    domainExtensions: Map[String, Set[String]],
    renderOptions: RenderOptions
) extends ApplicableMetaFieldRenderProvider {
  override def fieldsFor(element: AmfObject, renderOptions: RenderOptions): Seq[Field] = {
    val fields = super.fieldsFor(element, renderOptions)
    val obj    = element.meta

    fields ++ getExtensionFields(element, obj, extensionsFor(obj, domainExtensions))
  }

  private def extensionsFor(obj: Obj, domainExtensions: Map[String, Set[String]]) = {
    obj.`type`
      .flatMap(valueType => domainExtensions.get(valueType.iri()))
      .foldLeft(Set[String]()) { (acc, curr) =>
        acc ++ curr
      }
  }

  private def getExtensionFields(element: AmfObject, obj: Obj, extensionIris: Set[String]): Seq[Field] = {
    val fieldsNotInObj = diffByIri(element.fields.fieldsMeta(), obj.fields)
    fieldsNotInObj.filter(x => extensionIris.contains(x.value.iri()))
  }

  // TODO: had to do this because when implementing hashCode in the Field case class, a lot of other entries popped up in JSON-LD
  private def diffByIri(fields: List[Field], otherFields: List[Field]): List[Field] = {
    val similar = (a: Field, b: Field) => a.equals(b)
    fields.filterNot { a =>
      otherFields.exists(b => similar(a, b))
    }
  }
}
