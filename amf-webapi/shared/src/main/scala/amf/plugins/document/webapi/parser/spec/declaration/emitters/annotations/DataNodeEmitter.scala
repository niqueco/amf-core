package amf.plugins.document.webapi.parser.spec.declaration.emitters.annotations

import amf.core.emitter.BaseEmitters.{LinkScalaEmitter, NullEmitter, TextScalarEmitter, pos}
import amf.core.emitter.{Emitter, EntryEmitter, PartEmitter, SpecOrdering}
import amf.core.errorhandling.ErrorHandler
import amf.core.model.domain._
import amf.core.parser.{Annotations, Position}
import amf.core.utils.AmfStrings
import amf.core.vocabulary.Namespace
import amf.validations.RenderSideValidations.RenderValidation
import org.yaml.model.YDocument.{EntryBuilder, PartBuilder}
import org.yaml.model.{YNode, YType}

import scala.collection.mutable

case class DataNodeEmitter(
    dataNode: DataNode,
    ordering: SpecOrdering,
    referencesCollector: mutable.Map[String, DomainElement] = mutable.Map())(implicit eh: ErrorHandler)
    extends PartEmitter {
  private val xsdString: String  = Namespace.XsdTypes.xsdString.iri()
  private val xsdInteger: String = Namespace.XsdTypes.xsdInteger.iri()
  private val xsdFloat: String   = Namespace.XsdTypes.xsdFloat.iri()
  private val amlNumber: String  = Namespace.XsdTypes.amlNumber.iri()
  private val xsdDouble: String  = Namespace.XsdTypes.xsdDouble.iri()
  private val xsdBoolean: String = Namespace.XsdTypes.xsdBoolean.iri()
  private val xsdNil: String     = Namespace.XsdTypes.xsdNil.iri()

  override def emit(b: PartBuilder): Unit = {
    dataNode match {
      case scalar: ScalarNode => emitScalar(scalar, b)
      case array: ArrayNode   => emitArray(array, b)
      case obj: ObjectNode    => emitObject(obj, b)
      case link: LinkNode     => emitLink(link, b)
    }
  }

  def emitters(): Seq[EntryEmitter] = {
    val (knownEmitters, invalidEmitters) = emittersFor(dataNode).partition {
      case _: TextScalarEmitter | _: NullEmitter | _: EntryEmitter => true
      case _                                                       => false
    }
    invalidEmitters.foreach { raiseRenderViolation }
    knownEmitters.collect {
      case e: EntryEmitter      => e
      case t: TextScalarEmitter => new TextValueEmitter(t)
      case n: NullEmitter       => new NullValueEmitter(n)
    }
  }

  private def raiseRenderViolation(emitter: Emitter): Unit = {
    eh.violation(
      RenderValidation,
      dataNode.id,
      None,
      s"Unsupported seq of emitter type in data node emitters $emitter",
      dataNode.position(),
      dataNode.location()
    )
  }

  private def emittersFor(dataNode: DataNode) = dataNode match {
    case scalar: ScalarNode => Seq(scalarEmitter(scalar))
    case array: ArrayNode   => arrayEmitters(array)
    case obj: ObjectNode    => objectEmitters(obj)
    case link: LinkNode     => linkEmitters(link)
  }

  private def objectEmitters(objectNode: ObjectNode): Seq[EntryEmitter] = {
    objectNode
      .propertyFields()
      .map { f =>
        val value = objectNode.fields.getValue(f)
        DataPropertyEmitter(f.value.name.urlComponentDecoded,
                            value.value.asInstanceOf[DataNode],
                            ordering,
                            referencesCollector,
                            value.annotations)
      }
      .toSeq
  }

  private def emitObject(objectNode: ObjectNode, b: PartBuilder): Unit = {
    b.obj { b =>
      val ordered = ordering.sorted(objectEmitters(objectNode))
      ordered.foreach(_.emit(b))
    }
  }

  private def arrayEmitters(arrayNode: ArrayNode): Seq[PartEmitter] =
    arrayNode.members.map(DataNodeEmitter(_, ordering, referencesCollector))

  private def emitArray(arrayNode: ArrayNode, b: PartBuilder): Unit = {
    b.list { b =>
      ordering.sorted(arrayEmitters(arrayNode)).foreach(_.emit(b))
    }
  }

  private def emitScalar(scalar: ScalarNode, b: PartBuilder): Unit = scalarEmitter(scalar).emit(b)

  private def emitLink(link: LinkNode, b: PartBuilder): Unit = linkEmitters(link).foreach(_.emit(b))

  private def linkEmitters(link: LinkNode): Seq[PartEmitter] = {
    link.linkedDomainElement.foreach(elem => referencesCollector.update(link.alias.value(), elem))
    Seq(LinkScalaEmitter(link.alias.value(), link.annotations))
  }

  private def scalarEmitter(scalar: ScalarNode): PartEmitter = {
    scalar.dataType.option() match {
      case Some(t) if t == xsdString  => TextScalarEmitter(scalar.value.value(), scalar.annotations)
      case Some(t) if t == xsdInteger => TextScalarEmitter(scalar.value.value(), scalar.annotations, YType.Int)
      case Some(t) if t == xsdDouble | t == amlNumber =>
        TextScalarEmitter(scalar.value.value(), scalar.annotations, YType.Float)
      case Some(t) if t == xsdBoolean => TextScalarEmitter(scalar.value.value(), scalar.annotations, YType.Bool)
      case Some(t) if t == xsdNil     => NullEmitter(scalar.annotations)
      case _                          => TextScalarEmitter(scalar.value.value(), Annotations())
    }
  }

  override def position(): Position = pos(dataNode.annotations)

  private class NullValueEmitter(wrapped: NullEmitter) extends EntryEmitter {
    override def emit(b: EntryBuilder): Unit = b.entry(YNode("@value"), wrapped.emit(_))
    override def position(): Position        = wrapped.position()
  }

  private class TextValueEmitter(wrapped: TextScalarEmitter) extends EntryEmitter {
    override def emit(b: EntryBuilder): Unit = b.entry(YNode("@value"), wrapped.value)

    override def position(): Position = wrapped.position()
  }
}
