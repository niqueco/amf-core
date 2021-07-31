package amf.core.internal.remote

import amf.core.internal.remote.Mimes._

/**
  * Syntax
  */
object Syntax {

  sealed trait Syntax {
    val extension: String
  }

  case object Yaml extends Syntax {
    override val extension: String = "yaml"
  }

  case object Protobuf extends Syntax {
    override val extension: String = "proto"
  }

  case object GraphQL extends Syntax {
    override val extension: String = "graphql"
  }

  case object Json extends Syntax {
    override val extension: String = "json"
  }

  case object PlainText extends Syntax {
    override val extension: String = "txt"
  }

  val yamlMimes = Set(`TEXT/YAML`, `TEXT/X-YAML`, `TEXT/VND.YAML`, `APPLICATION/YAML`, `APPLICATION/X-YAML`,
    `APPLICATION/RAML+YAML`, `APPLICATION/OPENAPI+YAML`, `APPLICATION/SWAGGER+YAML`, `APPLICATION/ASYNCAPI+YAML`,
    `APPLICATION/ASYNC+YAML`)

  val jsonMimes = Set(`APPLICATION/JSON`, `APPLICATION/RAML+JSON`, `APPLICATION/OPENAPI+JSON`,
    `APPLICATION/SWAGGER+JSON`, `APPLICATION/ASYNCAPI+JSON`, `APPLICATION/ASYNC+JSON`)

  val proto3Mimes  = Set(`APPLICATION/GRPC`, `APPLICATION/GRPC+PROTO`, `APPLICATION/X-PROTOBUF`,
    `APPLICATION/PROTOBUF`, `APPLICATION/PROTOBUF_`, `APPLICATION/VND_GOOGLE`)

  val graphQLMimes  = Set(`APPLICATION/GRAPHQL`)

  /** Attempt to resolve [[Syntax]] from [[Mimes]]. */
  def unapply(mime: Option[String]): Option[Syntax] = mime match {
    case Some(m) if yamlMimes.contains(m) => Some(Yaml)
    case Some(m) if jsonMimes.contains(m) => Some(Json)
    case Some(m) if proto3Mimes.contains(m) => Some(Protobuf)
    case Some(m) if graphQLMimes.contains(m) => Some(GraphQL)
    case _ => None
  }
}
