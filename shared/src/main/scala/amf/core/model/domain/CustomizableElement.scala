package amf.core.model.domain

import amf.core.metamodel.domain.DomainElementModel.CustomDomainProperties
import amf.core.model.domain.extensions.DomainExtension

trait CustomizableElement { element: AmfObject =>

  def customDomainProperties: Seq[DomainExtension] = fields.field(CustomDomainProperties)
  def withCustomDomainProperties(extensions: Seq[DomainExtension]): this.type =
    setArray(CustomDomainProperties, extensions)

  def withCustomDomainProperty(extensions: DomainExtension): this.type =
    add(CustomDomainProperties, extensions)

}
