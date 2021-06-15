package amf.core.internal.plugins.namespace

import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.vocabulary.NamespaceAliases
import amf.core.internal.plugins.AMFPlugin

trait NamespaceAliasesPlugin extends AMFPlugin[BaseUnit] {
  def aliases(unit: BaseUnit): NamespaceAliases
}
