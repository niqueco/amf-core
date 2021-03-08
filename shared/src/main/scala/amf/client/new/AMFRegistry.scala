package amf.client.`new`

import amf.ProfileName
import amf.client.`new`.amfcore.{AMFParsePlugin, AMFPlugin, AMFValidatePlugin, LowPriority, ParsingInfo, PluginPriority}
import amf.core.Root
import amf.core.client.ParsingOptions
import amf.core.errorhandling.ErrorHandler
import amf.core.model.document.{BaseUnit, ExternalFragment}
import amf.core.model.domain.{DomainElement, ExternalDomainElement}
import amf.core.parser.{ParsedDocument, ParserContext, ReferenceHandler, SimpleReferenceHandler, SyamlParsedDocument}
import amf.core.remote.{Aml, Vendor}
import org.yaml.model.YDocument

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

case class AMFRegistry(plugins: PluginsRegistry,
                       entitiesRegistry: EntitiesRegistry,
                       //                       resolutionPipelines: Map[Name, AmfResolutionPipeline],
                       //                       contraintsRules: Map[ProfileName, Rules],
                       /*private [amf] var env:AmfEnvironment*/) {

  def withPlugin(amfPlugin: AMFPlugin[_]): AMFRegistry = copy(plugins = plugins.withPlugin(amfPlugin))
  def withPlugins(amfPlugins: List[AMFPlugin[_]]): AMFRegistry = copy(plugins = plugins.withPlugins(amfPlugins))
}

object AMFRegistry{

//  private val FullPluginRegistr = new PluginsRegistry(List(), List(), List(), GuessingParsePlugin)
  private val AllEntities = new EntitiesRegistry(Map.empty, Map.empty)
//
//  private val AmlEntities = new EntitiesRegistry()

  val empty = new AMFRegistry(PluginsRegistry.empty,EntitiesRegistry.empty)
//  val aml = new AmfRegistry(PluginsRegistry(List(AmlParsePlugin), List(AmlResolvePlugin), List(AmlValidationPlugin),ExternalFragmentParsePlugin),AmlEntities, Map.empty) //aml resolution pipeline

}
private[amf] trait DomainParsingFallback {

  def chooseFallback(element: ParsingInfo, availablePlugins: Seq[AMFParsePlugin]): BaseUnit
}

object ExternalFragmentDomainFallback extends DomainParsingFallback {
  override def chooseFallback(element: ParsingInfo, availablePlugins: Seq[AMFParsePlugin]): BaseUnit = {
    val document = element.parsed
    ExternalFragment()
      .withId(document.location)
      .withLocation(document.location)
      .withEncodes(
        ExternalDomainElement()
          .withRaw(document.raw)
          .withMediaType(document.mediatype))
  }
}
// maps or just lists?
case class PluginsRegistry private[amf] (parsePlugins: List[AMFParsePlugin],
                                         validatePlugins: List[AMFValidatePlugin],
                                         domainParsingFallback: DomainParsingFallback) {

  lazy val allPlugins: List[AMFPlugin[_]] = parsePlugins ++ validatePlugins

  def withPlugin(plugin: AMFPlugin[_]): PluginsRegistry = {
    plugin match {
      case p: AMFParsePlugin if !parsePlugins.exists(_.id == p.id) =>
        copy(parsePlugins = parsePlugins :+ p)
      case v: AMFValidatePlugin =>
        copy(validatePlugins = validatePlugins :+ v)
      case _ => this
    }
  }

  def withPlugins(plugins: List[AMFPlugin[_]]): PluginsRegistry = {
    plugins.foldLeft(this) { case (registry, plugin) => registry.withPlugin(plugin) }
  }

//  def ydocument(document: ParsedDocument):Option[YDocument] = document match {
//    case s:SyamlParsedDocument => Some(s.document)
//    case _ => None
//  }
//
//  def getParsePluginFor(document:ParsedDocument, vendor: Vendor): AmfParsePlugin = pickPlugin(document, (p:AmfParsePlugin, d:YDocument) => p.apply(d, vendor))
//
//
//  private def pickPlugin(document:ParsedDocument,selectorFn: (AmfParsePlugin,YDocument) => Boolean) = {
//    ydocument(document).flatMap { ast =>
//      reduceList(parsePlugins.filter(p => selectorFn(p, ast)))
//    } getOrElse(defaultPlugin)
//  }
//
//  private def reduceList(list:List[AmfParsePlugin]): Option[AmfParsePlugin] = {
//    list match {
//      case Nil         => None
//      case head :: Nil => Some(head)
//      case multiple    => Some(multiple.min)
//    }
//  }
//
//  def getParsePluginFor(document:ParsedDocument): AmfParsePlugin = pickPlugin(document, (p:AmfParsePlugin, d:YDocument) => p.applies(d))


//  def getResolvePluginFor(bu: BaseUnit, vendor: Vendor): Option[AmfResolvePlugin]

  def getValidationsPlugin(bu: BaseUnit): Seq[AMFValidatePlugin] = ???

  def getValidationPlugin(bu: BaseUnit, profile: ProfileName): Option[AMFValidatePlugin] = ???
}

object PluginsRegistry{
  val empty = PluginsRegistry(Nil,Nil, ExternalFragmentDomainFallback)
}

case class EntitiesRegistry(domainEntities: Map[String, DomainElement], wrappersRegistry: Map[String, DomainElement]) {}

object EntitiesRegistry {
  val empty = EntitiesRegistry(Map.empty, Map.empty)
}
