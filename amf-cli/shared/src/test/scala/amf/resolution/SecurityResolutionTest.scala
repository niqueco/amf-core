package amf.resolution

import amf.core.client.scala.config.RenderOptions
import amf.core.internal.remote.{Amf, Oas20, Oas20JsonHint, Raml10, Raml10YamlHint}
import amf.testing.AmfJsonLd

class SecurityResolutionTest extends ResolutionTest {

  override val basePath = "amf-cli/shared/src/test/resources/resolution/security/"

  multiGoldenTest("Security resolution raml to AMF", "security.raml.%s") { config =>
    cycle("security.raml",
          config.golden,
          Raml10YamlHint,
          target = AmfJsonLd,
          renderOptions = Some(config.renderOptions))
  }

  multiGoldenTest("Security resolution oas to AMF", "security.json.%s") { config =>
    cycle("security.json",
          config.golden,
          Oas20JsonHint,
          target = AmfJsonLd,
          renderOptions = Some(config.renderOptions))
  }

  override def defaultRenderOptions: RenderOptions = RenderOptions().withSourceMaps.withPrettyPrint
}
