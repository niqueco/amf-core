package amf.core.internal.registries.domain

import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.validation.payload.AMFPayloadValidationPlugin

import scala.collection.mutable

object AMFPluginsRegistry {
  // all static registries will end up here, and with a mayor version release the AMFGraphConfiguration will not be static
  private[amf] var staticConfiguration: AMFGraphConfiguration = AMFGraphConfiguration.predefined()

  private val payloadValidationPluginRegistry: mutable.HashMap[String, Seq[AMFPayloadValidationPlugin]] =
    mutable.HashMap()
  private val payloadValidationPluginIDRegistry: mutable.HashMap[String, AMFPayloadValidationPlugin] =
    mutable.HashMap()

  def cleanMediaType(mediaType: String): String =
    if (mediaType.contains(";")) mediaType.split(";").head
    else mediaType

  def dataNodeValidatorPluginForMediaType(mediaType: String): Seq[AMFPayloadValidationPlugin] =
    payloadValidationPluginRegistry.getOrElse(mediaType, Nil)

  def registerPayloadValidationPlugin(validationPlugin: AMFPayloadValidationPlugin): Unit = {
    payloadValidationPluginIDRegistry.get(validationPlugin.ID) match {
      case Some(_) =>
      case None =>
        validationPlugin.payloadMediaType.foreach { mt =>
          payloadValidationPluginRegistry.get(mt) match {
            case Some(list) if !list.contains(validationPlugin) =>
              payloadValidationPluginRegistry.update(mt, list :+ validationPlugin)
              payloadValidationPluginIDRegistry.update(validationPlugin.ID, validationPlugin)
            case None =>
              payloadValidationPluginRegistry.update(mt, Seq(validationPlugin))
              payloadValidationPluginIDRegistry.update(validationPlugin.ID, validationPlugin)
            case Some(_) => // ignore
          }
        }
    }
  }
}
