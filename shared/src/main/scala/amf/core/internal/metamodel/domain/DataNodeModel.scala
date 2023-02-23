package amf.core.internal.metamodel.domain

import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.{Array, Iri, Str}
import amf.core.internal.metamodel.domain.common.NameFieldSchema
import amf.core.client.scala.model.domain._
import amf.core.client.scala.vocabulary.Namespace.Data
import amf.core.client.scala.vocabulary.{Namespace, ValueType}
import amf.core.internal.metamodel.domain.federation.{HasFederationMetadataModel, HasShapeFederationMetadataModel}

/** Data Model to parse any generic data structure defined by recursive records with arrays and scalar values (think of
  * JSON or RAML) into a RDF graph.
  *
  * This can be used to parse value of annotations, payloads or examples
  */
object DataNodeModel extends DomainElementModel with NameFieldSchema with HasShapeFederationMetadataModel {

  // We set this so it can be re-used in the definition of the dynamic types
  override val fields: List[Field]     = List(Name, FederationMetadata) ++ DomainElementModel.fields
  override val `type`: List[ValueType] = Data + "Node" :: DomainElementModel.`type`

  override def modelInstance =
    throw new Exception("DataNode is an abstract class and it cannot be instantiated directly")

  override val doc: ModelDoc = ModelDoc(
    ModelVocabularies.Data,
    "DataNode",
    "Base class for all data nodes parsed from the data structure"
  )
}
