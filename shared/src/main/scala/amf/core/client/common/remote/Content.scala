package amf.core.client.common.remote

import amf.core.client.scala.lexer.{CharSequenceStream, CharStream}

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

// TODO: Should remove @JSExportAll. ApiDesigner does stream.toString() and that's the reason why we can't hide these unusable params (stream, mime)
@JSExportAll
case class Content(stream: CharStream, url: String, mime: Option[String] = None) {

  @JSExportTopLevel("client.remote.Content")
  def this(stream: String, url: String) = this(new CharSequenceStream(url, stream), url)

  @JSExportTopLevel("client.remote.Content")
  def this(stream: String, url: String, mime: String) = this(new CharSequenceStream(url, stream), url, Some(mime))

  def this(stream: String, url: String, mime: Option[String]) = this(new CharSequenceStream(url, stream), url, mime)

  override def toString: String = stream.toString
}
