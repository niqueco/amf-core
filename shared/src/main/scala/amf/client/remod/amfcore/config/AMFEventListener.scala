package amf.client.remod.amfcore.config

import amf.core.model.document.BaseUnit

private[remod] trait AMFEventListener {
  def event(eventKind: String, bu: BaseUnit)
}
