package com.mule.data
import amf.core.client.scala.model.DataType
import amf.core.client.scala.model.document.{Document, ExternalFragment, Fragment}
import amf.core.client.scala.model.domain.{ArrayNode, ObjectNode, ScalarNode}

object DataNodes {

  val name: ScalarNode =
    ScalarNode("name", Some(DataType.String)).withId("amf://name")

  val age: ScalarNode =
    ScalarNode("age", Some(DataType.Integer)).withId("amf://age")

  val happy: ScalarNode =
    ScalarNode("happy", Some(DataType.Boolean)).withId("amf://happy")

  val tags: ArrayNode = ArrayNode().withId("amf://tags")
  tags.addMember(happy)

  val person: ObjectNode = ObjectNode()
    .withId("amf://person")
    .addProperty("tags", tags)
    .addProperty("name", name)
    .addProperty("age", age)

  /** Document encoding simple ObjectNode. */
  val document: Document = Document()
    .withId("amf://document")
    .withLocation("http://document")
    .withRoot(true)
    .withEncodes(person)

  val recursive: ObjectNode = ObjectNode()
    .withId("amf://recursive")
    .addProperty("name", name)
    .addProperty("age", age)

  recursive.addProperty("father", recursive)

  /** Fragment encoding recursive ObjectNode. */
  val fragment: Fragment = ExternalFragment()
    .withId("amf://fragment")
    .withLocation("http://fragment")
    .withRoot(false)
    .withEncodes(recursive)

  val complex: Document = Document()
    .withId("amf://complex")
    .withLocation("http://complex")
    .withEncodes(name)
    .withDeclares(Seq(age, happy))
    .withReferences(Seq(document))

  val duplicateIds: Document = Document()
    .withId("amf://duplicate")
    .withEncodes(
        ObjectNode()
          .withId("amf://duplicate")
          .addProperty("name", ScalarNode("name", Some(DataType.String)).withId("amf://duplicate")))
}
