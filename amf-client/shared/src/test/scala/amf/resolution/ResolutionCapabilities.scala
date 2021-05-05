package amf.resolution

import amf.core.errorhandling.UnhandledErrorHandler
import amf.core.model.document.BaseUnit
import amf.core.remote._
import amf.core.services.RuntimeResolver
import amf.plugins.document.webapi.resolution.pipelines.{AmfEditingPipeline, AmfResolutionPipeline}

trait ResolutionCapabilities {
  protected def transform(unit: BaseUnit, pipeline: String, vendor: Vendor): BaseUnit = vendor match {
    case AsyncApi | AsyncApi20 | Raml08 | Raml10 | Oas20 | Oas30 =>
      RuntimeResolver.resolve(vendor.name, unit, pipeline, UnhandledErrorHandler)
    case Amf    => UnhandledAmfPipeline(pipeline).transform(unit, UnhandledErrorHandler)
    case target => throw new Exception(s"Cannot resolve $target")
    //    case _ => unit
  }

  object UnhandledAmfPipeline {
    def apply(pipeline: String) = pipeline match {
      case AmfEditingPipeline.name    => AmfEditingPipeline()
      case AmfResolutionPipeline.name => AmfResolutionPipeline()
      case _                          => throw new Exception(s"Cannot amf pipeline: $pipeline")
    }
  }
}
