package amf.client.remod

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
private[remod] class AMFClient(env: BaseEnvironment) {


  def getEnvironment:BaseEnvironment = env
  // how do we return the ErrorHandler that is created by the provider?
  // relation between EH vs BU? should the BU have an EH inside?
  // should parse always return error handler + base unit? (Amf result)

  // sync or async?

  // content type format, pendiente
  def parse(url: String, vendor: Option[Vendor] = None): Future[AmfResult] = AMFParser.parse(url, env)
    // build parsing context?

  def resolve(bu: BaseUnit): AmfResult = AMFResolver.resolve(bu, env) // clone? BaseUnit.resolved

  def render(bu:BaseUnit, target:Vendor):String = AMFRender.render(bu, env)
  // bu.clone?
  def validate(bu: BaseUnit, profileName: ProfileName): AMFValidationReport = ??? // how we can handle the parsing validations? error handler at base unit?

  // render

}
