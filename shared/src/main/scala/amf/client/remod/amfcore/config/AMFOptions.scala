package amf.client.remod.amfcore.config

import amf.core.client.ParsingOptions
import amf.core.emitter.RenderOptions

// TODO both options here are mutable and must be replaced
case class AMFOptions(parsingOptions: ParsingOptions, renderingOptions:RenderOptions /*, private[amf] var env:AmfEnvironment*/){
  //  def withPrettyPrint(): AmfEnvironment = {
  //    val copied = copy(renderingOptions = renderingOptions.withPrettyPrint)
  //    val newEnv = env.copy(options = copied)
  //    copied.env = newEnv
  //    newEnv
  //  }
}

object AMFOptions {
  def default() = new AMFOptions(ParsingOptions(), RenderOptions())
}
