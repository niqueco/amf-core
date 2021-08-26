package amf.core.client.scala.resource

import amf.core.client.common.remote.Content
import amf.core.client.scala.lexer.CharSequenceStream

import java.io.{Closeable, FileNotFoundException, InputStream}
import scala.concurrent.Future
import scala.io.BufferedSource
import scala.io.Source.fromInputStream
import scala.util.{Failure, Success, Try}

object ClasspathResourceLoader extends ResourceLoader {

  override def fetch(resource: String): Future[Content] = {
    Future.successful {
      Content(new CharSequenceStream(getContent(resource)), resource)
    }
  }

  override def accepts(resource: String): Boolean = {
    getClass.getResource(resource) != null
  }

  def getContent(path: String): String =
    readTextFileWithTry(getClass.getResourceAsStream(path)) match {
      case Success(v) => v
      case Failure(e) => throw new FileNotFoundException(path).initCause(e)
    }

  private def readTextFileWithTry(inputStream: InputStream): Try[String] = {
    Try {
      using(fromInputStream(inputStream)) { source: BufferedSource =>
        source.mkString
      }
    }
  }

  private def using[A <: Closeable, B](resource: A)(f: A => B): B = {
    try f(resource)
    finally resource.close()
  }
}
