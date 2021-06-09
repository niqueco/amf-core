package amf.core.internal.plugins.domain.shapes.models

import amf.core.internal.metamodel.domain.ShapeModel
import amf.core.client.scala.model.domain.{AmfScalar, Shape}
import amf.core.internal.parser.domain.Annotations
import org.yaml.model.YMapEntry
import org.yaml.render.YamlRender

trait ShapeHelper { this: Shape =>

  def setDefaultStrValue(entry: YMapEntry): Unit = {
    val str = entry.value.asScalar match {
      case Some(s) => s.text
      case _       => YamlRender.render(entry.value)
    }
    this.set(ShapeModel.DefaultValueString, AmfScalar(str, Annotations(entry.value)), Annotations(entry))
  }

}
