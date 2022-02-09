package amf.core.parser

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.internal.plugins.parse.AMFGraphParsePlugin
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext

class JsonLdContextOverrideTest extends AsyncFunSuite with Matchers {

  override implicit def executionContext: ExecutionContext = ExecutionContext.Implicits.global

  test("JSON-LD @context can be overriden by stepping on or adding new aliases") {
    val aliases     = Map("doc" -> "http://a.ml/vocabularies/document#", "data" -> "http://a.ml/vocabularies/data#")
    val plugin      = AMFGraphParsePlugin(aliases)
    val errorClient = AMFGraphConfiguration.predefined().baseUnitClient()
    val validClient = AMFGraphConfiguration.predefined().withPlugin(plugin).baseUnitClient()
    val jsonld      = "file://shared/src/test/resources/parser/unexpected-context.flattened.jsonld"
    for {
      errorResult <- errorClient.parse(jsonld)
      _           <- errorResult.conforms shouldBe false
      validResult <- validClient.parse(jsonld)
    } yield {
      validResult.conforms shouldBe true
    }
  }
}
