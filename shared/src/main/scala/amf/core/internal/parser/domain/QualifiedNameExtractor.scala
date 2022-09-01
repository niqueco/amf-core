package amf.core.internal.parser.domain

import amf.core.internal.utils.QName

trait QualifiedNameExtractor {
  def apply(name: String): QName
}

object DotQualifiedNameExtractor extends QualifiedNameExtractor {
  override def apply(name: String): QName = QName(name)
}

object JsonPointerQualifiedNameExtractor extends QualifiedNameExtractor {
  override def apply(name: String): QName = name.split("#").toList match {
    case name :: Nil       => QName("", name)
    case Nil               => QName("", name)
    case namespace :: name => QName(namespace, name.mkString("#"))
  }
}
