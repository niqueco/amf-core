package amf.core.parser

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.model.document.Document
import amf.core.client.scala.parse.document.SyamlParsedDocument
import amf.core.internal.metamodel.document.DocumentModel
import amf.core.internal.parser.{AMFCompiler, CompilerContextBuilder}
import amf.core.internal.utils.UriUtils.platform
import org.scalatest.{FunSuite, Matchers}

class FieldsTest extends FunSuite with Matchers {

  test("obtain value of certain field through iri") {
    val document    = Document().withLocation("some location")
    val locationIri = DocumentModel.Location.value.iri()
    assert(document.fields.getValueAsOption(locationIri).nonEmpty)
  }
}
