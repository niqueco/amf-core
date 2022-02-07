package amf.core.internal.utils

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class UtilsTest extends AnyFunSuite with Matchers {

  case class ResolveUrl(base: String, url: String)

  val resolveTests: List[(ResolveUrl, String)] = List(
    ResolveUrl("file://some/dir/api.raml", "./../external file.json") -> "file://some/external%20file.json",
    ResolveUrl("file://some/api.raml", "./dir with space/file.json")  -> "file://some/dir%20with%20space/file.json",
    ResolveUrl("file://", "./file.json")                              -> "file://file.json",
    ResolveUrl("file:///", "./file.json")                             -> "file:///file.json",
    ResolveUrl("file://some/dir/api.raml", "http://some.example/")    -> "http://some.example/",
    ResolveUrl("file://some/dir/api.raml", "#/some/fragment")         -> "file://some/dir/api.raml#/some/fragment",
    ResolveUrl("file://dir/other/directory/", "../file.json")         -> "file://dir/other/file.json",
    ResolveUrl("file://dir/other/directory/file.json", "")            -> "file://dir/other/directory/",
    ResolveUrl("file:///", "../file.json")                             -> "file:///../file.json",
  )

  resolveTests.foreach {
    case (input, expected) =>
      val ResolveUrl(base, url) = input
      val result                = UriUtils.resolveRelativeTo(base, url)
      test(s"Resolved uri $expected") {
        result should be(expected)
      }
  }

  val urlResolveTests: List[(String, String)] = List(
    "file://resources/input.yaml"            -> "file://resources/input.yaml",
    "file://resources/ignored/../input.yaml" -> "file://resources/input.yaml",
    "file://../dataType.yaml"                -> "file://../dataType.yaml",
    "file:///../dataType.yaml"                -> "file:///../dataType.yaml",
  )

  urlResolveTests.foreach {
    case (input, output) =>
      test(s" the url $input should be resolved to $output") {
        UriUtils.resolvePath(input) should be(output)
      }
  }

  case class RemoveFileName(path: String, so: String)

  val stripFileNameTests: List[(RemoveFileName, String)] = List(
    RemoveFileName("file://some/dir/api.raml", "nux") -> "file://some/dir/",
    RemoveFileName("file://some/dir/api.raml", "mac") -> "file://some/dir/",
    RemoveFileName("file://some/dir/", "mac")         -> "file://some/dir/",
    RemoveFileName("file://some/dir", "mac")          -> "file://some/dir/",
    RemoveFileName("c:\\directory\\file.json", "win") -> "c:\\directory\\"
  )

  stripFileNameTests.foreach {
    case (input, expected) =>
      val result = UriUtils.stripFileName(input.path, input.so)
      test(s"Stripped file name result $expected from $input") {
        result should be(expected)
      }
  }

  test("Escape special chars") {
    val testStringNewline = "test\ntest"
    testStringNewline.escape should be("test\\ntest")
    val testStringQuotes = s""""test""""
    testStringQuotes.escape should be("\"test\"")
    val testStringIsoControl = '\u0013'.toString
    testStringIsoControl.escape should be("\\u13")
    val testStringNormal = "test"
    testStringNormal.escape should be(testStringNormal)
  }

  test("QName") {
    val qNameWithoutDot = "test"
    QName(qNameWithoutDot) should be(QName("", qNameWithoutDot))
    val qNameWithDot = "test.dot"
    QName(qNameWithDot) should be(QName("test", "dot"))
    val emptyString = ""
    QName(emptyString) should be(QName("", ""))
  }

  val urlNormalizationTests: List[(String, String)] = List(
    "file://correct/absolute/path" -> "file://correct/absolute/path",
    "/normal/absolute/path"        -> "file://normal/absolute/path",
    "http:protocol"                -> "http:protocol",
    "https:protocol"               -> "https:protocol",
    "file:protocol"                -> "file:protocol",
    "jar:protocol"                 -> "jar:protocol",
    "something/random"             -> "file://something/random",
  )

  urlNormalizationTests.foreach {
    case (input, output) =>
      test(s" the url $input should be normalized to $output") {
        AmfStrings(input).normalizeUrl should be(output)
      }
  }
}
