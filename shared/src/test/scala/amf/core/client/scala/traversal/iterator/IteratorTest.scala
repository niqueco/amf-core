package amf.core.client.scala.traversal.iterator
import amf.core.client.scala.model.document.Document
import amf.core.client.scala.model.document.FieldsFilter.Local
import amf.core.client.scala.model.domain.{AmfObject, DomainElement}
import amf.core.internal.metamodel.document.FragmentModel
import com.mule.data.DataNodes
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

trait IteratorTest extends AnyFunSuite with Matchers {

  test("Complete iterator (simple document)") {
    val it = AmfElementStrategy.iterator(List(DataNodes.document), IdCollector())
    it.size should be(20)
  }

  test("Complete iterator (simple document) with instance collector") {
    val it = AmfElementStrategy.iterator(List(DataNodes.document), InstanceCollector())
    it.size should be(20)
  }

  test("Complete iterator (recursive fragment)") {
    val it = AmfElementStrategy.iterator(List(DataNodes.fragment), IdCollector())
    it.size should be(15)
  }

  test("Domain element iterator (recursive fragment) collect") {
    val ids =
      DataNodes.fragment
        .iterator(strategy = DomainElementStrategy)
        .collect { case e: DomainElement => e.id }
        .to(LazyList)

    ids should contain inOrderOnly ("amf://recursive", "amf://name", "amf://age")
  }

  test("Amf element iterator (recursive fragment) collect first") {
    val location = AmfElementStrategy.iterator(List(DataNodes.fragment), IdCollector()).collectFirst {
      case obj: AmfObject if obj.fields.?(FragmentModel.Location).isDefined =>
        obj.fields.get(FragmentModel.Location).toString
    }

    location shouldBe Some("http://fragment")
  }

  test("Domain element iterator (complex document) with local fields") {
    val complex: Document = DataNodes.complex
    val ids = complex
      .iterator(strategy = DomainElementStrategy, fieldsFilter = Local)
      .collect { case e: DomainElement => e.id }
      .to(LazyList)
    ids should contain inOrderOnly ("amf://name", "amf://age", "amf://happy")
  }

  test("Amf element iterator with instance collector for iterating node with duplicate ids") {
    val it = AmfElementStrategy.iterator(List(DataNodes.duplicateIds), InstanceCollector())
    it.size should be(9)
  }

  test("Domain element iterator with instance collector for iterating node with duplicate ids") {
    val it = DomainElementStrategy.iterator(List(DataNodes.duplicateIds), InstanceCollector())
    it.size should be(2) // object node and scalar node
  }

}
