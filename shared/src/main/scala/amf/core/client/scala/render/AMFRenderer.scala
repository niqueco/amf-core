package amf.core.client.scala.render

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.remote.Vendor.AMF
import amf.core.internal.render.AMFSerializer
import org.yaml.model.YDocument

object AMFRenderer {

  def render(bu: BaseUnit, configuration: AMFGraphConfiguration): String = render(bu, AMF.mediaType, configuration)

  def renderAST(bu: BaseUnit, configuration: AMFGraphConfiguration): YDocument = ???

  /**
    *
    * @param bu
    * @param target media type which specifies a vendor, and optionally a syntax.
    * @param env
    * @return
    */
  def render(bu: BaseUnit, mediaType: String, configuration: AMFGraphConfiguration): String =
    new AMFSerializer(bu, mediaType, configuration.renderConfiguration).renderToString

  def renderAST(bu: BaseUnit, mediaType: String, configuration: AMFGraphConfiguration): YDocument = ???

}
