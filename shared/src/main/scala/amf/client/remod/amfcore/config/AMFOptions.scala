package amf.client.remod.amfcore.config

/**
  * A set of options to customize parsing and rendering
  * @param parsingOptions {@link amf.client.remod.amfcore.config.ParsingOptions}
  * @param renderOptions {@link amf.client.remod.amfcore.config.RenderOptions}
  */
private[amf] case class AMFOptions(parsingOptions: ParsingOptions,
                                   renderOptions: RenderOptions /*, private[amf] var env:AmfEnvironment*/ ) {
  //  def withPrettyPrint(): AmfEnvironment = {
  //    val copied = copy(renderingOptions = renderingOptions.withPrettyPrint)
  //    val newEnv = env.copy(options = copied)
  //    copied.env = newEnv
  //    newEnv
  //  }
}

object AMFOptions {

  /** Creates a default {@link amf.client.remod.amfcore.config.AMFOptions} */
  def default() = new AMFOptions(ParsingOptions(), RenderOptions())
}
