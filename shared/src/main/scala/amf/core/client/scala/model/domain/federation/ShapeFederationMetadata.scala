package amf.core.client.scala.model.domain.federation

import amf.core.internal.parser.domain.{Annotations, Fields}
import amf.core.internal.metamodel.domain.federation.ShapeFederationMetadataModel
import org.yaml.model.YMap

case class ShapeFederationMetadata(fields: Fields, annotations: Annotations) extends FederationMetadata {
  override def meta: ShapeFederationMetadataModel.type = ShapeFederationMetadataModel
  override private[amf] def componentId                = s"/federation-metadata"
}

object ShapeFederationMetadata {
  def apply(): ShapeFederationMetadata                         = apply(Annotations())
  def apply(ast: YMap): ShapeFederationMetadata                = apply(Annotations(ast))
  def apply(annotations: Annotations): ShapeFederationMetadata = ShapeFederationMetadata(Fields(), annotations)
}
