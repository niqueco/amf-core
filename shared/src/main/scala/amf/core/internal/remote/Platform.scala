package amf.core.internal.remote

import amf.core.client.common.AmfExceptionCode
import amf.core.client.common.remote.Content
import amf.core.client.platform.model.AmfObjectWrapper
import amf.core.client.scala.AMFGraphConfiguration
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.{AmfObject, DomainElement}
import amf.core.client.scala.resource.ResourceLoader
import amf.core.internal.metamodel.Obj
import org.mulesoft.common.io.{AsyncFile, FileSystem, SyncFile}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

trait FileMediaType {
  def mimeFromExtension(extension: String): Option[String] =
    extension match {
      case "graphql" | "gql"               => Some(Mimes.`application/graphql`)
      case "proto" | "pb"                  => Some(Mimes.`application/x-protobuf`)
      case "json"                          => Some(Mimes.`application/json`)
      case "yaml" | "yam" | "yml" | "raml" => Some(Mimes.`application/yaml`)
      case "jsonld" | "amf"                => Some(Mimes.`application/ld+json`)
      case "nt"                            => Some(Mimes.`text/n3`)
      case _                               => None
    }

  def extension(path: String): Option[String] = {
    Some(path.lastIndexOf(".")).filter(_ > 0).map(dot => path.substring(dot + 1))
  }
}

object FileMediaType extends FileMediaType

/** */
trait Platform extends FileMediaType {

  def name: String = "gen"

  def findCharInCharSequence(s: CharSequence)(p: Char => Boolean): Option[Char]

  /** Underlying file system for platform. */
  val fs: FileSystem

  def exit(code: Int): Unit = System.exit(code)

  def stdout(text: String): Unit = System.out.println(text)

  def stdout(e: Throwable): Unit = System.out.println(e)

  def stderr(text: String): Unit = System.err.println(text)

  def stderr(ex: Exception): Unit = System.err.println(ex)

  val wrappersRegistry: mutable.HashMap[String, (AmfObject) => AmfObjectWrapper]             = mutable.HashMap.empty
  val wrappersRegistryFn: mutable.HashMap[(Obj) => Boolean, (AmfObject) => AmfObjectWrapper] = mutable.HashMap.empty

  def registerWrapper(model: Obj)(builder: (AmfObject) => AmfObjectWrapper): Option[AmfObject => AmfObjectWrapper] =
    wrappersRegistry.put(model.`type`.head.iri(), builder)

  def registerWrapperPredicate(p: (Obj) => Boolean)(
      builder: (AmfObject) => AmfObjectWrapper
  ): Option[AmfObject => AmfObjectWrapper] =
    wrappersRegistryFn.put(p, builder)

  def wrap[T <: AmfObjectWrapper](entity: AmfObject): T = entity match {
    case e: DomainElement =>
      wrappersRegistry.get(e.meta.`type`.head.iri()) match {
        case Some(builder) => builder(entity).asInstanceOf[T]
        case None          => wrapFn(e)
      }
    case d: BaseUnit =>
      wrappersRegistry.get(d.meta.`type`.head.iri()) match {
        case Some(builder) => builder(entity).asInstanceOf[T]
        case None          => wrapFn(d)
      }
    case o: AmfObject if wrappersRegistry.contains(o.meta.`type`.head.iri()) =>
      val builder = wrappersRegistry(o.meta.`type`.head.iri())
      builder(entity).asInstanceOf[T]
    case null => null.asInstanceOf[T] // TODO solve this in a better way
    case _    => wrapFn(entity)
  }

  def wrapFn[T <: AmfObjectWrapper](entity: AmfObject): T = entity match {
    case e: DomainElement =>
      wrappersRegistryFn.keys.find(p => p(e.meta)) match {
        case Some(k) => wrappersRegistryFn(k)(e).asInstanceOf[T]
        case None => {
          throw new Exception(s"Cannot find builder for object meta ${e.meta}")
        }
      }
    case d: BaseUnit =>
      wrappersRegistryFn.keys.find(p => p(d.meta)) match {
        case Some(k) => wrappersRegistryFn(k)(d).asInstanceOf[T]
        case None    => throw new Exception(s"Cannot find builder for object meta ${d.meta}")
      }
    case _ => throw new Exception(s"Cannot build object of type $entity")
  }

  private def loaderConcat(url: String, loaders: Seq[ResourceLoader])(implicit
      executionContext: ExecutionContext
  ): Future[Content] = loaders.toList match {
    case Nil         => throw new UnsupportedUrlScheme(url)
    case head :: Nil => head.fetch(url)
    case head :: tail =>
      head.fetch(url).recoverWith { case _ =>
        loaderConcat(url, tail)
      }
  }

  /** Resolve remote url. */
  def fetchContent(url: String, configuration: AMFGraphConfiguration)(implicit
      executionContext: ExecutionContext
  ): Future[Content] =
    loaderConcat(url, configuration.getResourceLoaders.filter(_.accepts(url)))

  /** Platform out of the box [ResourceLoader]s */
  def loaders()(implicit executionContext: ExecutionContext): Seq[ResourceLoader]

  /** encodes a complete uri. Not encodes chars like / */
  def encodeURI(url: String): String

  /** decode a complete uri. */
  def decodeURI(url: String): String

  /** encodes a uri component, including chars like / and : */
  def encodeURIComponent(url: String): String

  /** decodes a uri component */
  def decodeURIComponent(url: String): String

  /** either decodes a uri component or returns raw url */
  def safeDecodeURIComponent(url: String): Either[String, String] =
    try {
      Right(decodeURIComponent(url))
    } catch {
      case _: Throwable => Left(url)
    }

  /** Location where the helper functions for custom validations must be retrieved */
  protected def customValidationLibraryHelperLocation: String = "http://a.ml/amf/validation.js"

  /** Write specified content on given url. */
  def write(url: String, content: String)(implicit executionContext: ExecutionContext): Future[Unit] = {
    url match {
      case File(path) => writeFile(path, content)
      case _          => Future.failed(new Exception(s"Unsupported write operation: $url"))
    }
  }

  /** Return temporary directory. */
  def tmpdir(): String

  /** Return the OS (win, mac, nux). */
  def operativeSystem(): String

  /** Write specified content on specified file path. */
  protected def writeFile(path: String, content: String)(implicit executionContext: ExecutionContext): Future[Unit] =
    fs.asyncFile(path).write(content)

}

object Platform {
  def base(url: String): Option[String] = Some(url.substring(0, url.lastIndexOf('/')))
}

object HttpParts {
  def unapply(uri: String): Option[(String, String, String)] = uri match {
    case url if url.startsWith(HTTP_PROTOCOL) || url.startsWith(HTTPS_PROTOCOL) =>
      val protocol        = url.substring(0, url.indexOf("://") + 3)
      val rightOfProtocol = url.stripPrefix(protocol)
      val host =
        if (rightOfProtocol.contains("/")) rightOfProtocol.substring(0, rightOfProtocol.indexOf("/"))
        else rightOfProtocol
      val path = rightOfProtocol.replace(host, "")
      Some(protocol, host, path)
    case _ => None
  }

  val HTTP_PROTOCOL  = "http://"
  val HTTPS_PROTOCOL = "https://"
}

object File {
  val FILE_PROTOCOL = "file://"

  def unapply(url: String): Option[String] = {
    url match {
      case s if s.startsWith(FILE_PROTOCOL) =>
        val path = s.stripPrefix(FILE_PROTOCOL)
        Some(path)
      case _ => None
    }
  }
}

object Relative {
  def unapply(url: String): Option[String] = {
    url match {
      case s if !s.contains(":") => Some(s)
      case _                     => None
    }
  }
}

/** Unsupported file system. */
object UnsupportedFileSystem extends FileSystem {

  override def syncFile(path: String): SyncFile = unsupported

  override def asyncFile(path: String): AsyncFile = unsupported

  override def separatorChar: Char = unsupported

  private def unsupported = throw new Exception(s"Unsupported operation")
}

case class FileNotFound(cause: Throwable)
    extends FileLoaderException(AmfExceptionCode.FileNotFound, "File Not Found: " + cause.getMessage, cause)

case class SocketTimeout(cause: Throwable)
    extends FileLoaderException(AmfExceptionCode.SocketTimeout, "Socket Timeout: " + cause.getMessage, cause)

case class NetworkError(cause: Throwable)
    extends FileLoaderException(AmfExceptionCode.NetworkError, "Network Error: " + cause.getMessage, cause)

case class UnexpectedStatusCode(resource: String, statusCode: Int)
    extends AmfException(
      AmfExceptionCode.UnexpectedStatusCode,
      s"Unexpected status code '$statusCode' for resource '$resource'"
    )

class UnsupportedUrlScheme(url: String)
    extends AmfException(AmfExceptionCode.UnsupportedUrlScheme, "Unsupported Url scheme: " + url)

class PathResolutionError(message: String)
    extends AmfException(AmfExceptionCode.PathResolutionError, "Error resolving path: " + message)

abstract class FileLoaderException(code: String, msj: String, e: Throwable) extends AmfException(code, msj, e)

abstract class AmfException(val code: String, val message: String, e: Throwable = new Throwable())
    extends Exception(message, e)
