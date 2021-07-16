package amf.core.internal.convert

import amf.core.internal.convert.CoreClientConverters._
import amf.core.client.platform.transform
import amf.core.client.scala.transform.{TransformationPipeline, TransformationStep}

// not defined in CoreBaseConverter because conversion makes use of ClientList.
object TransformationPipelineConverter {
  implicit object TransformationPipelineMatcher
      extends BidirectionalMatcher[TransformationPipeline, transform.TransformationPipeline] {
    override def asClient(from: TransformationPipeline): transform.TransformationPipeline = {
      new transform.TransformationPipeline {
        override val name: String                                    = from.name
        override def steps: ClientList[transform.TransformationStep] = from.steps.asClient
      }
    }
    override def asInternal(from: transform.TransformationPipeline): TransformationPipeline = {
      new TransformationPipeline {
        override val name: String                   = from.name
        override def steps: Seq[TransformationStep] = from.steps.asInternal
      }
    }
  }
}
