package amf.core.client.scala.parse.document

import amf.core.client.scala.model.domain.{DomainElement, Linkable}
import amf.core.internal.parser.domain.DeclarationPromise
import amf.core.internal.validation.CoreValidations.UnresolvedReference

trait UnresolvedReference { this: DomainElement =>
  val reference: String

  // Unresolved references to things that can be linked
  var ctx: Option[UnresolvedComponents] = None

  def withContext(c: UnresolvedComponents): DomainElement = {
    ctx = Some(c)
    this
  }

  def futureRef(resolve: Linkable => Unit): Unit = ctx match {
    case Some(c) =>
      c.futureDeclarations.futureRef(
          id,
          reference,
          DeclarationPromise(
              resolve,
              () =>
                c.eh.violation(
                    UnresolvedReference,
                    this,
                    None,
                    s"Unresolved reference '$reference'"
                )
          )
      )
    case _ => throw new Exception("Cannot create unresolved reference with missing parsing context")
  }

}
