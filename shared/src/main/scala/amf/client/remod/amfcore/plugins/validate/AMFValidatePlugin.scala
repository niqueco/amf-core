package amf.client.remod.amfcore.plugins.validate

import amf.client.remod.amfcore.plugins.AMFPlugin
import amf.core.model.document.BaseUnit
import amf.core.validation.AMFValidationReport

trait AMFValidatePlugin extends AMFPlugin[BaseUnit] {
  def validate: AMFValidationReport
}
