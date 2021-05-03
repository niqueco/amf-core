package amf.client.resource

import java.io.FileNotFoundException
import java.util.concurrent.CompletableFuture

import amf.client.execution.BaseExecutionEnvironment
import amf.client.remote.Content
import amf.core.lexer.FileStream
import amf.core.remote.FileMediaType._
import amf.core.remote.FutureConverter._
import amf.core.remote.{FileNotFound, JvmPlatform}
import amf.core.utils.AmfStrings

import scala.concurrent.{ExecutionContext, Future}

case class FileResourceLoader(executionContext: ExecutionContext) extends BaseFileResourceLoader {

  implicit val exec: ExecutionContext = executionContext

  def this() = this(JvmPlatform.instance().defaultExecutionEnvironment.executionContext)
  def this(executionEnvironment: BaseExecutionEnvironment) = this(executionEnvironment.executionContext)

  def fetchFile(resource: String): CompletableFuture[Content] = {
    println(s"FileResourceLoader.fetchFile $resource started")
    Future {
      try {
        println(s"FileResourceLoader.fetchFile $resource succeeded")
        Content(new FileStream(resource),
                ensureFileAuthority(resource),
                extension(resource).flatMap(mimeFromExtension))
      } catch {
        case e: FileNotFoundException =>
          // exception for local file system where we accept spaces [] and other chars in files names
          println(s"FileResourceLoader.fetchFile $resource failed")
          val decoded = resource.urlDecoded
          println(s"FileResourceLoader.fetchFile ${resource.urlDecoded} started")
          try {
            Content(new FileStream(decoded),
                    ensureFileAuthority(resource),
                    extension(resource).flatMap(mimeFromExtension))
            println(s"FileResourceLoader.fetchFile ${resource.urlDecoded} succeeded")
          } catch {
            case e: FileNotFoundException => {
              println(s"FileResourceLoader.fetchFile ${resource.urlDecoded} failed")
              throw FileNotFound(e)
            }
          }
      }
    }.asJava
  }

  def ensureFileAuthority(str: String): String = if (str.startsWith("file:")) str else s"file://$str"
}
