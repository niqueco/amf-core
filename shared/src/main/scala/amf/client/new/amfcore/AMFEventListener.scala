package amf.client.`new`.amfcore

import amf.core.model.document.BaseUnit

trait AMFEventListener {
  def event(eventKind: String, bu: BaseUnit)
}
