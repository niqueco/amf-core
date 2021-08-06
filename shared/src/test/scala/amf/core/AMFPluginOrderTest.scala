package amf.core

import amf.core.client.common.{HighPriority, LowPriority, NormalPriority}
import org.scalatest.{FunSuite, Matchers}

class AMFPluginOrderTest extends FunSuite with Matchers {

  test("Plugin list should be ordered by priority") {
    val a            = DummyParsePlugin("1", "A", LowPriority)
    val b            = DummyParsePlugin("2", "B", HighPriority)
    val c            = DummyParsePlugin("3", "C", NormalPriority)
    val unsortedList = Seq(a, b, c)
    val sortedList   = unsortedList.sorted
    sortedList should contain theSameElementsInOrderAs List(b, c, a)
  }
}
