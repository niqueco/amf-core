package amf.core.common

import amf.core.internal.utils.TSort
import amf.core.internal.utils.TSort.tsort
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

/** [[TSort]] test
  */
class TSortTest extends AnyFunSuite with Matchers {

  test("Topological sort with edges") {
    val edges = List(
      ("a0", "b1"),
      ("a0", "b2"),
      ("b1", "c1"),
      ("b2", "c2"),
      ("a1", "b3"),
      ("b3", "c3"),
      ("b3", "c4")
    )
    tsort(edges) should be(Right(List("a0", "a1", "b2", "b3", "b1", "c2", "c1", "c3", "c4")))
  }

  test("Topological sort with map") {
    val graph: Map[String, Set[String]] = Map(
      ("a0", Set()),
      ("b1", Set("a0")),
      ("b2", Set("a0")),
      ("c1", Set("b1")),
      ("c2", Set("b2")),
      ("a1", Set()),
      ("b3", Set("a1")),
      ("c3", Set("b3")),
      ("c4", Set("b3"))
    )
    tsort(graph, Seq()) should be(Right(List("a0", "a1", "b2", "b3", "b1", "c2", "c1", "c3", "c4")))
  }

  test("Topological sort with cycles") {
    val edges = List(
      ("a0", "a1"),
      ("a1", "a2"),
      ("a1", "a3"),
      ("a3", "a1"),
      ("a3", "a4")
    )
    tsort(edges) should be(Left(List("a0", "a4", "a3", "a1", "a2")))
  }
}
