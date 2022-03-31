package amf.core.client.common.remote

import amf.core.client.scala.lexer.{CharSequenceStream, CharStream}

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

case class Content(stream: CharStream, @JSExport url: String, mime: Option[String] = None) {

  @JSExportTopLevel("Content")
  def this(stream: String, url: String) = this(new CharSequenceStream(url, stream), url)

  @JSExportTopLevel("Content")
  def this(stream: String, url: String, mime: String) = this(new CharSequenceStream(url, stream), url, Some(mime))

  def this(stream: String, url: String, mime: Option[String]) = this(new CharSequenceStream(url, stream), url, mime)

  override def toString: String = stream.toString
}
