package amf.core.client.common.transform

import amf.core.internal.remote.MediaTypeParser

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportTopLevel("PipelineName")
@JSExportAll
object PipelineName {

  /**
    * Constructs a proper pipeline name given certain target media type and a pipeline id
    * @param targetMediaType Provide a specification for obtaining the correct pipeline.
    *                        Must be <code>"application/spec"</code> or <code>"application/spec+syntax"</code>.
    *                        Examples: <code>"application/raml10"</code> or <code>"application/raml10+yaml"</code>
    * @param pipelineId must be one of the values defined in [[PipelineId]]
    * @return a string representing the pipeline name
    */
  def from(targetMediaType: String, pipelineId: String): String = {
    val vendorExp = new MediaTypeParser(targetMediaType).getPureVendorExp
    s"$vendorExp+$pipelineId"
  }

}

@JSExportTopLevel("PipelineId")
@JSExportAll
object PipelineId {
  val Default       = "default"
  val Editing       = "editing"
  val Compatibility = "compatibility"
  val Cache         = "cache"
}
