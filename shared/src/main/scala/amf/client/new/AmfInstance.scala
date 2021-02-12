package amf.client.`new`

import amf.ProfileName
import amf.client.convert.CoreClientConverters.platform
import amf.client.parse.DefaultParserErrorHandler
import amf.core.client.ParsingOptions
import amf.core.errorhandling.ErrorHandler
import amf.core.model.document.BaseUnit
import amf.core.remote.{Cache, Context, Vendor}
import amf.core.resolution.pipelines.ResolutionPipeline
import amf.core.services.RuntimeCompiler
import amf.core.validation.AMFValidationReport

import scala.concurrent.Future


// client
class AmfClient(env: BaseEnvironment) {


  def getEnvironment:BaseEnvironment = env
  // como hacemos para devolver el ErrorHandler que nos genero si lo pide para un BU dado?
  // relacion EH vs BU? el BU DEBE tener el error handler adentro? (si no fue de parseo?)
  // el parse siempre devuelve error handler + base unit? (Amf result)

  // y el vendor? sobrecargamos el metodo de parse? un objeto aparte?
  // sync or async?

  // content type format, pendiente
  def parse(url: String, vendor: Option[Vendor] = None): Future[AmfResult] = AmfParser.parse(url, env)
    // build parsing context?

  def resolve(bu: BaseUnit): AmfResult = AmfResolver.resolve(bu, env) // clone? BaseUnit.resolved

  def render(bu:BaseUnit, target:Vendor):String = AmfRender.render(bu, env)
  // bu.clone?
  def validate(bu: BaseUnit, profileName: ProfileName): AMFValidationReport = ??? // how we can handle the parsing validations? error handler at base unit?

  // render

}
