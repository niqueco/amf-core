package amf.core.internal.plugins.document.graph.entities

import amf.core.internal.entities.Entities
import amf.core.internal.metamodel.{ModelDefaultBuilder, Obj}
import amf.core.internal.metamodel.domain._

private[amf] object DataNodeEntities extends Entities {

  override protected val innerEntities: Seq[ModelDefaultBuilder] = Seq(
      ObjectNodeModel,
      ScalarNodeModel,
      ArrayNodeModel,
      LinkNodeModel
  )
}
