package amf.core.internal.remote

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MediaTypeParserTest extends AnyFunSpec with Matchers {

  case class Fixture(source: String, spec: String, syntax: Option[String])
  val fixture = Seq(
      Fixture("application/raml", "application/raml", None),
      Fixture("application/raml10", "application/raml10", None),
      Fixture("application/raml+yaml", "application/raml+yaml", Some("application/yaml")),
      Fixture("application/oas20+yaml", "application/oas20+yaml", Some("application/yaml")),
      Fixture("text/plain", "text/plain", None),
      Fixture("text/plain+vnd", "text/plain+vnd", Some("text/vnd"))
  )

  fixture.foreach { f =>
    describe(s"A mediaType ${f.source}") {
      val parser = new MediaTypeParser(f.source)
      it(s"have spec ${f.spec}") {
        parser.getVendorExp should be(f.spec)
      }
      f.syntax.foreach(s => {
        it(s"should have syntax $s") {
          parser.getSyntaxExp.get should be(s)
        }
      })
    }
  }

}
