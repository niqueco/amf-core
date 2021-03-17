package amf.client.remod.amfcore.config

import amf.client.remod.{AMFIdGenerator, PathAMFIdGenerator$}
import amf.core.execution.ExecutionEnvironment
import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets

import java.util.EventListener

class AMFConfig(private val logger: AMFLogger,
                private val listeners: List[EventListener],
                val executionContext: ExecutionEnvironment,
                private val idGenerator: AMFIdGenerator)

object AMFConfig extends PlatformSecrets{
  def predefined() = new AMFConfig(MutedLogger, Nil, ExecutionEnvironment(), PathAMFIdGenerator$)
}
