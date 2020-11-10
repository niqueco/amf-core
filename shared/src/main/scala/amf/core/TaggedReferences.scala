package amf.core

import amf.core.annotations.ReferenceTargets
import amf.core.model.document.BaseUnit
import amf.core.parser.{Range, Reference}

object TaggedReferences {

  implicit class BuReferenceTagger(bu: BaseUnit) {
    def tagReferences(root: Root): BaseUnit = {
      val t = root.references.flatMap { r =>
        taggedReferences(r.unit.location().getOrElse(r.unit.id), r.origin)
      }.toMap
      val targets = ReferenceTargets(t)
      bu.add(targets)
    }

    def tagReference(location:String,r: Reference): BaseUnit = {
      val rta: ReferenceTargets = bu.annotations.find(classOf[ReferenceTargets]) match {
        case Some(rt) =>
          bu.annotations.reject(_.isInstanceOf[ReferenceTargets])
          rt ++ taggedReferences(location,r)
        case _ => ReferenceTargets(taggedReferences(location, r))
      }
      bu.add(rta)
    }

  }

  def taggedReferences(location: String, r: Reference): Map[String, Seq[Range]] =
    Map(location -> r.refs.map(_.reduceToLocation()))
}
