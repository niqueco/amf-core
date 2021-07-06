package amf.core.client.scala

/**
  * Contains common AMF graph operations not associated to documents.
  * Base client for <code>AMLElementClient</code> and <code>AMLElementClient</code>.
  */
class AMFGraphElementClient private[amf] (protected val configuration: AMFGraphConfiguration) {

  def getConfiguration: AMFGraphConfiguration = configuration
}
