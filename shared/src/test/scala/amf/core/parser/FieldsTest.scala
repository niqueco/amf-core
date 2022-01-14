package amf.core.parser

import amf.core.client.scala.model.document.Document
import amf.core.internal.metamodel.document.DocumentModel
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class FieldsTest extends AnyFunSuite with Matchers {

  test("obtain value of certain field through iri") {
    val document    = Document().withLocation("some location")
    val locationIri = DocumentModel.Location.value.iri()
    assert(document.fields.getValueAsOption(locationIri).nonEmpty)
  }
}
