package amf.plugins.domain.shapes.models

import amf.core.metamodel.{Field, Obj}
import amf.core.metamodel.domain.ExternalSourceElementModel
import amf.core.model.domain._
import amf.core.model.{BoolField, StrField}
import amf.core.parser.{Annotations, Fields}
import amf.plugins.domain.shapes.metamodel.ExampleModel
import amf.plugins.domain.shapes.metamodel.ExampleModel._
import org.yaml.model.YPart
import amf.core.utils.Strings
import amf.plugins.document.webapi.parser.spec.common.PayloadSerializer

/**
  *
  */
class Example(override val fields: Fields, override val annotations: Annotations)
    extends DomainElement
    with Linkable
    with NamedDomainElement
    with ExternalSourceElement
    with PayloadSerializer {

  def displayName: StrField     = fields.field(DisplayName)
  def description: StrField     = fields.field(Description)
  def structuredValue: DataNode = fields.field(StructuredValue)
  def strict: BoolField         = fields.field(Strict)
  def mediaType: StrField       = fields.field(MediaType)

  def withDisplayName(displayName: String): this.type = set(DisplayName, displayName)
  def withDescription(description: String): this.type = set(Description, description)
  def withValue(value: String): this.type             = set(ExternalSourceElementModel.Raw, value)
  def withStructuredValue(value: DataNode): this.type = set(StructuredValue, value)
  def withStrict(strict: Boolean): this.type          = set(Strict, strict)
  def withMediaType(mediaType: String): this.type     = set(MediaType, mediaType)

  override def linkCopy(): Example = Example().withId(id)

  override def meta: Obj = ExampleModel

  /** Value , path + field value that is used to compose the id when the object its adopted */
  override def componentId: String = "/example/" + name.option().getOrElse("default-example").urlComponentEncoded

  def toJson: String = toJson(this)

  def toYaml: String = toYaml(this)

  /** apply method for create a new instance with fields and annotations. Aux method for copy */
  override protected def classConstructor: (Fields, Annotations) => Linkable with DomainElement = Example.apply
  override protected def nameField: Field                                                       = Name
}

object Example {

  def apply(): Example = apply(Annotations())

  def apply(ast: YPart): Example = apply(Annotations(ast))

  def apply(annotations: Annotations): Example = Example(Fields(), annotations)

  def apply(fields: Fields, annotations: Annotations): Example = new Example(fields, annotations)
}