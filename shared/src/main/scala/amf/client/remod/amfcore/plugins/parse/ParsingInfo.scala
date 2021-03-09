package amf.client.remod.amfcore.plugins.parse

import amf.core.Root

// YDocument will have to change, we need to use a container which is not attached to syaml
// Root is has a lot of information that is not used, can be limited to YDocument and raw string
case class ParsingInfo(parsed: Root, vendor: Option[String])
