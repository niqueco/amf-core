package amf.client.`new`

import amf.ProfileName
import amf.client.`new`.amfcore.plugins.{ExternalFragmentParsePlugin, GuessingParsePlugin}
import amf.client.`new`.amfcore.{AmfParsePlugin, AmfResolutionPipeline, AmfResolvePlugin, AmfValidatePlugin}
import amf.core.model.document.BaseUnit
import amf.core.model.domain.DomainElement
import amf.core.parser.{ParsedDocument, SyamlParsedDocument}
import amf.core.remote.{Aml, Vendor}
import org.yaml.model.YDocument

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

case class AmfRegistry(plugins: PluginsRegistry,
                       entitiesRegistry: EntitiesRegistry,
                       resolutionPipelines: Map[Name, AmfResolutionPipeline],
                       contraintsRules: Map[ProfileName, Rules],
                       private [amf] var env:AmfEnvironment) {

  private val pendingDialects: mutable.Set[String] = mutable.Set.empty


  private[amf] def init() = {
    if(pendingDialects.isEmpty) Future.unit
    else {
      val instance = env.getInstance()
      val eventualResults = pendingDialects.map(p => instance.parse(p, Some(Aml)))
      Future.sequence(eventualResults).map(r => r.foreach {
        case d:Dialect => amlRegistry.register(d)
        case _ => // ignore
      })
    }
  }

  def withPlugin(amfPlugin: AmfParsePlugin): AmfRegistry = {
    val newPlugins = amfPlugin match {
      case p:AmfParsePlugin => plugins.copy(parsePlugins = p +: plugins.parsePlugins)
      case v:AmfValidatePlugin => plugins.copy(validatePlugins = v :+ plugins.validatePlugins)
      case _ => plugins
    }
    copy(plugins= newPlugins)
  }
}

object AmfRegistry{

  private val FullPluginRegistr = new PluginsRegistry(List(), List(), List(), GuessingParsePlugin)
  private val AllEntities = new EntitiesRegistry()

  private val AmlEntities = new EntitiesRegistry()

  private val AllPipelines = Map()


  val forEntities = new AmfRegistry(PluginsRegistry.empty,AllEntities, Map.empty)
  val aml = new AmfRegistry(PluginsRegistry(List(AmlParsePlugin), List(AmlResolvePlugin), List(AmlValidationPlugin),ExternalFragmentParsePlugin),AmlEntities, Map.empty) //aml resolution pipeline

}
// maps or just lists?
case class PluginsRegistry private[amf] (parsePlugins: List[AmfParsePlugin],
                                         validatePlugins:List[AmfValidatePlugin],
                                         defaultPlugin: AmfParsePlugin) { // ?? default handling?){

  def ydocument(document: ParsedDocument):Option[YDocument] = document match {
    case s:SyamlParsedDocument => Some(s.document)
    case _ => None
  }

  def getParsePluginFor(document:ParsedDocument, vendor: Vendor): AmfParsePlugin = pickPlugin(document, (p:AmfParsePlugin, d:YDocument) => p.apply(d, vendor))


  private def pickPlugin(document:ParsedDocument,selectorFn: (AmfParsePlugin,YDocument) => Boolean) = {
    ydocument(document).flatMap { ast =>
      reduceList(parsePlugins.filter(p => selectorFn(p, ast)))
    } getOrElse(defaultPlugin)
  }

  private def reduceList(list:List[AmfParsePlugin]): Option[AmfParsePlugin] = {
    list match {
      case Nil         => None
      case head :: Nil => Some(head)
      case multiple    => Some(multiple.min)
    }
  }

  def getParsePluginFor(document:ParsedDocument): AmfParsePlugin = pickPlugin(document, (p:AmfParsePlugin, d:YDocument) => p.apply(d))


  def getResolvePluginFor(bu: BaseUnit, vendor: Vendor): Option[AmfResolvePlugin]

  def getValidationsPlugin(bu: BaseUnit): Seq[AmfValidatePlugin]

  def getValidationPlugin(bu: BaseUnit, profile: ProfileName): Option[AmfValidatePlugin]
}

object PluginsRegistry{
  val empty = PluginsRegistry(Nil,Nil,Nil, ExternalFragmentParsePlugin)
}

case class EntitiesRegistry(domainEntities: Map[String, DomainElement], wrappersRegistry: Map[String, DomainElement]) {}
