package amf.aml.internal.plugin

import amf.core.internal.annotations.serializable.CoreSerializableAnnotations
import amf.core.internal.entities.CoreEntities
import amf.core.internal.metamodel.Obj
import amf.core.internal.metamodel.document.SourceMapModel
import amf.core.internal.plugins.document.graph.entities.AMFGraphEntities
import amf.core.internal.registries.{AMFRegistry, RegistryContext}
import org.scalatest.{FunSuite, Matchers}

class RegistryContextTest extends FunSuite with Matchers {

  def defaultIri(metadata: Obj): String = metadata.`type`.head.iri()

  test("Test types without blacklist") {
    val ctx = RegistryContext(
        AMFRegistry.empty
          .withEntities(CoreEntities.entities ++ AMFGraphEntities.entities)
          .withAnnotations(CoreSerializableAnnotations.annotations))

    CoreEntities.entities.values.foreach { `type` =>
      val iri = defaultIri(`type`)
      shouldBeDefined(ctx.findType(iri))
    }

    CoreEntities.entities.values.filterNot(_ == SourceMapModel).foreach { `type` =>
      val instance = `type`.modelInstance
      instance.meta should be(`type`)
    }
  }

  test("Test types without Core") {
    val ctx = RegistryContext(
        AMFRegistry.empty
          .withEntities(AMFGraphEntities.entities)
          .withAnnotations(CoreSerializableAnnotations.annotations))

    CoreEntities.entities.values.foreach { `type` =>
      val iri = defaultIri(`type`)
      shouldBeEmpty(ctx.findType(iri))
    }

//    CoreEntities.entities.values.foreach { `type` =>
//      the[Exception] thrownBy {
//        `type`.
//      } should have message s"Cannot find builder for type ${`type`}"
//    }
  }

  private def shouldBeDefined[T](opt: Option[T]): Unit = {
    //  should be ('defined) not working on JS
    opt.isDefined should be(true)
  }

  private def shouldBeEmpty[T](opt: Option[T]): Unit = {
    //  should be ('empty) not working on JS
    opt.isDefined should be(false)
  }
}
