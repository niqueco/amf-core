package amf.client.`new`.clientusage

import amf.client.`new`.amfcore.{AmfParsePlugin, HighPriority, PluginPriority}
import amf.client.`new`.{AmfEnvironment, AmfParser, AmfResult}
import amf.client.remote.Content
import amf.core.Root
import amf.core.errorhandling.ErrorHandler
import amf.core.model.document.{BaseUnit, Document}
import amf.core.parser.{ParserContext, ReferenceHandler, SimpleReferenceHandler}
import amf.core.remote.{Amf, Aml, Raml10, Vendor}
import amf.internal.resource.ResourceLoader
import org.yaml.model.{YDocument, YType}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object AmfClientExample {

  def parseFromFile(): Future[Unit] = {
    val eventualResult = AmfEnvironment.webApi.getInstance().parse("file://myfile.raml")
    eventualResult.map {r =>
      if(r.conforms) println(s"Conforms document id: ${r.asDocument.encodes.id}")
      else println(s"Document has ${r.result.results.length} errors")
    }
  }

  def parseRemoteWithLoader(): Future[AmfResult] = {

    val httpRL =  new ResourceLoader {
      /** Fetch specified resource and return associated content. Resource should have benn previously accepted. */
      override def fetch(resource: String): Future[Content] = Future.successful(Content())

      /** Accepts specified resource. */
      override def accepts(resource: String): Boolean = resource.startsWith("http")
    }
    AmfEnvironment.webApi.withResourceLoader(httpRL).getInstance().parse("http://myremote.com#api")

  }

  def parseFileFromStream: Future[AmfResult] = {

    val myApi =
      """
        |#%RAML 1.0
        |title: test
        |""".stripMargin
    val eventualResult = AmfParser.parseStream(myApi, AmfEnvironment.webApi)
    eventualResult
  }

  def renderUnit = {
    val instance = AmfEnvironment.webApi.getInstance()
    instance.parse("file://myapi.raml").map { r =>
      instance.render(r.bu, Raml10)
    }
  }

  def resolveUnit = {
    val instance = AmfEnvironment.webApi.getInstance()
    instance.parse("file://myapi.raml").map { r =>
      instance.resolve(r.bu)
    }
  }

  def parseWithCustomPlugin = {
    val customVendor = new Vendor {
      override val name: String = "MyVendor"
    }

    val myPlugin  = new AmfParsePlugin {
      override def parse(document: Root, ctx: ParserContext): BaseUnit = { Document()}

      override val supportedVendors: Seq[Vendor] = Seq[customVendor]
      override val validVendorsToReference: Seq[Vendor] = Nil

      override def referenceHandler(eh: ErrorHandler): ReferenceHandler = SimpleReferenceHandler

      override def allowRecursiveReferences: Boolean = true

      override val id: String = "MyCustomPlugin"

      override def apply(element: YDocument): Boolean = element.node.tagType != YType.Null

      override def priority: PluginPriority = HighPriority
    }

    AmfEnvironment.onlyConfig.withPlugin(myPlugin).getInstance().parse("file://MyFile.raml", Some(customVendor))
  }

  def renderWithPrettyPrint = {
    val instance = AmfEnvironment.webApi.getInstance()
    instance.parse("file://myfile.raml").map { r =>
      instance.render(r.bu, Amf)
      instance.getEnvironment.options.withPrettyPrint().getInstance().render(r.bu, Amf)
    }
  }



}
