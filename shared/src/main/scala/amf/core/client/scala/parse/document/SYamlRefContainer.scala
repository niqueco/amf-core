package amf.core.client.scala.parse.document

import amf.core.client.common.position.Range
import org.yaml.model.YNode.MutRef
import org.yaml.model.{YNode, YPart, YScalar}

class SYamlRefContainer(override val linkType: ReferenceKind,
                        val node: YPart,
                        override val uriFragment: Option[String])
    extends ASTRefContainer(linkType, node.location, uriFragment) {

  override def reduceToLocation(): Range = node match {
    case n: YNode if n.value.isInstanceOf[YScalar] =>
      val s = n.value.asInstanceOf[YScalar]
      reduceStringLength(s, uriFragment.map(l => l.length + 1).getOrElse(0), if (s.mark.plain) 0 else 1)
    case mr: MutRef => Range(mr.origValue.range)
    case _          => Range(node.location.inputRange)
  }

  private def reduceStringLength(s: YScalar, fragmentLenght: Int, markSize: Int = 0): Range = {
    val inputRange =
      if (node.location.inputRange.columnTo < fragmentLenght && node.location.inputRange.lineFrom < node.location.inputRange.lineTo) {
        val lines = s.text.split('\n')
        lines.find(_.contains('#')) match {
          case Some(line) =>
            node.location.inputRange
              .copy(lineTo = node.location.inputRange.lineFrom + lines.indexOf(line), columnTo = line.indexOf('#') - 1)
          case _ => node.location.inputRange
        }
      } else {
        getRefValue.location.inputRange.copy(columnTo = node.location.inputRange.columnTo - fragmentLenght)
      }
    Range((inputRange.lineFrom, inputRange.columnFrom + markSize), (inputRange.lineTo, inputRange.columnTo - markSize))
  }

  private def getRefValue: YPart = node match {
    case ref: MutRef => ref.origValue
    case _           => node
  }

}

object SYamlRefContainer {
  def apply(linkType: ReferenceKind, node: YPart, uriFragment: Option[String]) =
    new SYamlRefContainer(linkType, node, uriFragment)
}
