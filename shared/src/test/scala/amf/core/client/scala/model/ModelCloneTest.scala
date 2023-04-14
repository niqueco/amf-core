package amf.core.client.scala.model
import amf.core.client.scala.model.document.Document
import amf.core.client.scala.model.domain._
import amf.core.client.scala.vocabulary.Namespace.{Data, XsdTypes}
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.annotations.{DefinedBySpec, ErrorDeclaration, TrackedElement}
import amf.core.internal.metamodel.domain.DomainElementModel
import amf.core.internal.metamodel.{Field, ModelDefaultBuilder, Obj}
import amf.core.internal.parser.domain.{Annotations, Fields}
import amf.core.internal.remote.Raml10
import amf.core.render.ElementsFixture
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable

class ModelCloneTest extends AnyFunSuite with ElementsFixture with Matchers {

  test("Test clone encoded at Document") {
    val cloned: Document = document.cloneUnit().asInstanceOf[Document]
    cloned.encodes.asInstanceOf[ScalarNode].withValue("ClonedValue")

    document.encodes.asInstanceOf[ScalarNode].value.value() should be("myValue")
    cloned.encodes.asInstanceOf[ScalarNode].value.value() should be("ClonedValue")
  }

  test("Test clone object node") {
    val cloned: ObjectNode = objectNode.cloneElement(mutable.Map.empty).asInstanceOf[ObjectNode]

    cloned.addProperty("newProp", ScalarNode("newValue", Some(DataType.String)))

    objectNode.allProperties().size should be(1)
    cloned.allProperties().size should be(2)

    cloned
      .allPropertiesWithName()("myProp1")
      .asInstanceOf[ArrayNode]
      .addMember(ScalarNode("new2", Some(DataType.String)))

    arrayNode.members.length should be(1)
    objectNode.allPropertiesWithName()("myProp1").asInstanceOf[ArrayNode].members.length should be(1)
    cloned.allPropertiesWithName()("myProp1").asInstanceOf[ArrayNode].members.length should be(2)
  }

  test("Test clone recursive object") {
    val cloned = recursiveObj.cloneElement(mutable.Map.empty).asInstanceOf[ObjectNode]

    cloned
      .allProperties()
      .head
      .asInstanceOf[ArrayNode]
      .members
      .head
      .asInstanceOf[ObjectNode]
      .allProperties()
      .head should be(cloned)
    cloned.allProperties().head.asInstanceOf[ArrayNode].members.head.asInstanceOf[ObjectNode].id should be(
      recursiveObj.allProperties().head.asInstanceOf[ArrayNode].members.head.asInstanceOf[ObjectNode].id
    )
    succeed
  }

  test("Test annotations at object") {
    objectNode.annotations += DefinedBySpec(Raml10)
    val cloned = objectNode.cloneElement(mutable.Map.empty).asInstanceOf[ObjectNode]

    cloned.annotations.contains(classOf[DefinedBySpec]) should be(true)
  }

  test("Test clone document with duplicated ids") {
    val localNode = ObjectNode()
      .withId(objectNode.id)
      .addProperty(
        "localProp",
        ScalarNode().withId("amf://localId").withDataType(XsdTypes.xsdString.iri()).withValue("aValue")
      )
    val doc              = Document().withId("amf://localDoc").withDeclares(Seq(objectNode, localNode))
    val cloned: Document = doc.cloneUnit().asInstanceOf[Document]
    val obj              = cloned.declares.head
    val local            = cloned.declares.last

    obj.id should be(local.id)

    obj.asInstanceOf[ObjectNode].allPropertiesWithName().keySet.head should be("myProp1")
    local.asInstanceOf[ObjectNode].allPropertiesWithName().keySet.head should be("localProp")
  }

  test("Test clone link node with internal linked domain element ") {
    val scalarNode = ScalarNode("linkValue", Some(XsdTypes.xsdString.iri())).withId("amf://linkNode1")
    val linkNode   = LinkNode("link", "linkValue").withId("amf://linkNode2").withLinkedDomainElement(scalarNode)

    val cloned = linkNode.cloneElement(mutable.Map.empty)
    cloned.linkedDomainElement.get.id should be(scalarNode.id)

  }

  test("Test clone with elements that have same hash code") {
    case class SomeType(fields: Fields, annotations: Annotations) extends DomainElement {
      override def meta: Obj = new ModelDefaultBuilder {
        override def fields: List[Field]      = Nil
        override val `type`: List[ValueType]  = Nil
        override def modelInstance: AmfObject = SomeType(Fields(), Annotations())
      }
      override def componentId: String = "someId"
      override def hashCode(): Int     = 1
    }

    val type1 = SomeType(Fields(), Annotations())
    type1.withId("amf://type-1-id")
    val type2 = SomeType(Fields(), Annotations())
    type2.withId("amf://type-1-id")

    val doc         = Document().withId("amf://id1").withDeclares(Seq(type1, type2))
    val clonedDoc   = doc.cloneUnit()
    val declares    = clonedDoc.asInstanceOf[Document].declares
    val type1Cloned = declares.head
    val type2Cloned = declares(1)

    // cloned instances are effectively different
    (type1 eq type1Cloned) should be(false)
    (type2 eq type2Cloned) should be(false)

    // when cloning document, both objects must be cloned
    (type1Cloned eq type2Cloned) should be(false)

  }

  test("test clone id for error declaration") {
    case class Dummy(fields: Fields = Fields(), annotations: Annotations = Annotations()) extends DomainElement {
      override def meta: DummyModel.type = DummyModel
      override def componentId: String   = ""
    }

    object DummyModel extends DomainElementModel {
      override def modelInstance: Dummy    = Dummy()
      override def fields: List[Field]     = Nil
      override val `type`: List[ValueType] = Data + "Dummy" :: DomainElementModel.`type`
    }

    case class Error() extends ErrorDeclaration[DummyModel.type] {
      override val namespace: String = "http://amferror.com/#MyErrorClass/"

      override val model: DummyModel.type = DummyModel

      override def newErrorInstance: ErrorDeclaration[DummyModel.type] = Error()

      /** Set of fields composing object. */
      override val fields: Fields = Fields()

      /** Value , path + field value that is used to compose the id when the object its adopted */
      override def componentId: String = "errorTrait"

      /** Set of annotations for element. */
      override val annotations: Annotations = Annotations()
    }

    val error = Error().withId("id1")

    val doc  = Document().withId("amf://id1").withDeclares(Seq(error))
    val unit = doc.cloneUnit()
    val head = unit.asInstanceOf[Document].declares.head
    head.id should be(error.id)
  }

  test("Test clone object with TrackedElement at branch") {
    scalarNode2.annotations += TrackedElement.fromInstance(objectNode)
    val cloned: ObjectNode = objectNode.cloneElement(mutable.Map.empty).asInstanceOf[ObjectNode]
    cloned.id = "http://cloned#id"
    val maybeElement = scalarNode2.annotations.find(classOf[TrackedElement])
    maybeElement.isDefined shouldBe (true)
    maybeElement.get.elements.left.toOption.get.head.id shouldBe ("amf://id2")

    val clonedTrackedElement =
      cloned.allProperties().head.asInstanceOf[ArrayNode].members.head.annotations.find(classOf[TrackedElement])
    clonedTrackedElement.isDefined shouldBe (true)
    clonedTrackedElement.get.elements.left.toOption.get.head.id shouldBe ("http://cloned#id")
  }

  test("Test clone object with TrackedElement Id out of branch") {
    scalarNode2.annotations.clear()
    scalarNode2.annotations += TrackedElement.apply("amf://idMissed")
    val cloned: ObjectNode = objectNode.cloneElement(mutable.Map.empty).asInstanceOf[ObjectNode]
    val maybeElement       = scalarNode2.annotations.find(classOf[TrackedElement])
    maybeElement.isDefined shouldBe (true)
    maybeElement.get.elements.toOption.get.head shouldBe ("amf://idMissed")

    val clonedTrackedElement =
      cloned.allProperties().head.asInstanceOf[ArrayNode].members.head.annotations.find(classOf[TrackedElement])
    clonedTrackedElement.isDefined shouldBe (true)
    clonedTrackedElement.get.elements.toOption.get.head shouldBe ("amf://idMissed")
  }
}
