package amf.core.remote.server

import java.io.IOException

import amf.client.remote.Content
import amf.client.resource.BaseFileResourceLoader
import amf.core.lexer.CharSequenceStream
import amf.core.remote.FileMediaType._
import amf.core.remote.FileNotFound
import org.mulesoft.common.io.Fs

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
import amf.core.utils.AmfStrings

@JSExportTopLevel("JsServerFileResourceLoader")
@JSExportAll
case class JsServerFileResourceLoader() extends BaseFileResourceLoader {
  override def fetchFile(resource: String): js.Promise[Content] = {
    println(s"JsServerFileResourceLoader.fetchFile $resource started")
    Fs.asyncFile(resource)
      .read()
      .map(content => {
        println(s"JsServerFileResourceLoader.fetchFile $resource succeeded")
        Content(new CharSequenceStream(resource, content),
                ensureFileAuthority(resource),
                extension(resource).flatMap(mimeFromExtension))
      })
      .recoverWith {
        case _: IOException => // exception for local file system where we accept resources including spaces
          println(s"JsServerFileResourceLoader.fetchFile $resource failed")
          println(s"JsServerFileResourceLoader.fetchFile ${resource.urlDecoded} started")
          Fs.asyncFile(resource.urlDecoded)
            .read()
            .map(content => {
              println(s"JsServerFileResourceLoader.fetchFile ${resource.urlDecoded} succeeded")
              Content(new CharSequenceStream(resource, content),
                      ensureFileAuthority(resource),
                      extension(resource).flatMap(mimeFromExtension))
            })
            .recover {
              case io: IOException => {
                println(s"JsServerFileResourceLoader.fetchFile ${resource.urlDecoded} failed")
                throw FileNotFound(io)
              }
            }
      }
      .toJSPromise
  }

  def ensureFileAuthority(str: String): String = if (str.startsWith("file:")) str else s"file://$str"
}
