package amf.core.client.common.render

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("JSONSchemaVersions")
object JSONSchemaVersions {
  import amf.core.client.common.render.{Unspecified => UnspecifiedObj}
  val Unspecified: JSONSchemaVersion = UnspecifiedObj
  val Draft04: JSONSchemaVersion     = JsonSchemaDraft4
  val Draft07: JSONSchemaVersion     = JsonSchemaDraft7
  val Draft201909: JSONSchemaVersion = JsonSchemaDraft201909
}

@JSExportAll
sealed trait JSONSchemaVersion

@JSExportAll
object Unspecified extends JSONSchemaVersion
@JSExportAll
object JsonSchemaDraft4 extends JSONSchemaVersion
@JSExportAll
object JsonSchemaDraft7 extends JSONSchemaVersion
@JSExportAll
object JsonSchemaDraft201909 extends JSONSchemaVersion
