package amf.core.internal.transform.stages.helpers

import amf.core.client.scala.model.document.{BaseUnit, EncodesModel}
import amf.core.client.scala.model.domain.DomainElement

class ModelReferenceResolver(model: BaseUnit) {

  def findFragment(url: String): Option[DomainElement] = {
    model match {
      case encodes: EncodesModel if model.location().exists(_.equals(url)) => Some(encodes.encodes)
      case _ if model.location().exists(_.equals(url))                     => None
      case _ =>
        var remaining                     = model.references.map(new ModelReferenceResolver(_))
        var result: Option[DomainElement] = None
        while (remaining.nonEmpty) {
          remaining.head.findFragment(url) match {
            case res: Some[DomainElement] =>
              result = res
              remaining = Nil
            case _ =>
              remaining = remaining.tail
          }
        }
        result
    }
  }
}
