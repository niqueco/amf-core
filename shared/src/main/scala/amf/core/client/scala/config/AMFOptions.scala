package amf.core.client.scala.config

import amf.core.client.scala.config

/** A set of options to customize parsing and rendering
  *
  * @param parsingOptions
  *   [[ParsingOptions]]
  * @param renderOptions
  *   [[RenderOptions]]
  */
private[amf] case class AMFOptions(
    parsingOptions: ParsingOptions,
    renderOptions: RenderOptions /*, private[amf] var configuration:AMFConfiguration*/
) {
  //  def withPrettyPrint(): AmfEnvironment = {
  //    val copied = copy(renderingOptions = renderingOptions.withPrettyPrint)
  //    val newEnv = env.copy(options = copied)
  //    copied.env = newEnv
  //    newEnv
  //  }
}

object AMFOptions {

  /** Creates a default [[AMFOptions]] */
  def default() = new AMFOptions(ParsingOptions(), config.RenderOptions())
}
