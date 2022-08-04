package amf.core.internal.plugins.syntax

import amf.core.client.scala.parse.document.{ParsedDocument, StringParsedDocument}
import amf.core.internal.plugins.syntax.StringDocBuilder.INDENTATION_WIDTH
import org.mulesoft.common.client.lexical.Position
import org.mulesoft.common.client.lexical.Position.{FIRST, ZERO}

import scala.collection.mutable

class SourceCodeBlock(val indentation: Integer, val lines: mutable.Buffer[(String, Position)] = mutable.Buffer()) {

  def sortedBuilder: mutable.StringBuilder = {
    val b            = stringBuilder()
    var previousLine = minPos.line
    lines.sortBy(_._2).foreach { case (l, p) =>
      while (previousLine + 1 < p.line) {
        b.append("\n")
        previousLine += 1
      }
      b.append(s"$l\n")
      val newLines = l.count(c => c == '\n')
      previousLine += (newLines + 1)
    }
    b
  }

  def sortedBuilderWithDelimiter(delimiter: String): mutable.StringBuilder = {
    val b = stringBuilder()
    lines.sortBy(_._2).map(_._1).zipWithIndex.foreach { case (l, idx) =>
      if (idx < lines.length - 1) {
        b.append(s"$l$delimiter")
      } else {
        b.append(l)
      }
    }
    b
  }

  def builder: mutable.StringBuilder = {
    val b = stringBuilder()
    lines.foreach { case (l, _) =>
      b.append(s"$l\n")
    }
    b
  }

  def minPos: Position = {
    val positions = lines.filter { case (_, p) =>
      p != ZERO && p != FIRST
    }
    positions.map(_._2).sorted.headOption.getOrElse(ZERO)
  }

  def +=(s: String, pos: Position = Position.ZERO): SourceCodeBlock = {
    lines.append((s"${" " * indentation}$s", pos))
    this
  }

  def merge(s: String, pos: Position = Position.ZERO): SourceCodeBlock = {
    lines.append((s, pos))
    this
  }

  private def stringBuilder(): StringBuilder = new mutable.StringBuilder()

  private def countOccurrences(src: String, tgt: String): Int = src.sliding(tgt.length).count(window => window == tgt)
}

object SourceCodeBlock {
  def apply(): SourceCodeBlock = new SourceCodeBlock(0)
}

object StringDocBuilder {
  val INDENTATION_WIDTH = 2
}

class StringDocBuilder(document: SourceCodeBlock = SourceCodeBlock()) extends ASTBuilder[SourceCodeBlock] {

  def inlined(f: StringDocBuilder => Unit): String = {
    val scb = sourceCodeBlock(0)
    val b   = new StringDocBuilder(scb)
    f(b)
    scb.builder.toString().replaceAll("\n", "")
  }

  def listWithDelimiter(delimiter: String)(f: StringDocBuilder => Unit): StringDocBuilder = {
    stringDocBuilder(f = f, toString = _.sortedBuilderWithDelimiter(delimiter).toString())
  }

  def list(f: StringDocBuilder => Unit): StringDocBuilder = {
    stringDocBuilder(f = f)
  }

  def fixed(f: StringDocBuilder => Unit): StringDocBuilder = {
    stringDocBuilder(f = f)
  }

  def obj(f: StringDocBuilder => Unit): StringDocBuilder = {
    stringDocBuilder(document.indentation + INDENTATION_WIDTH, f = f)
  }

  def doc(f: StringDocBuilder => Unit): StringDocBuilder = {
    val scb = new SourceCodeBlock(0, document.lines)
    val sdb = new StringDocBuilder(scb)
    f(sdb)
    sdb
  }

  def +=(s: String, pos: Position = Position.ZERO): SourceCodeBlock = document.+=(s, pos)

  def merge(s: String, pos: Position = Position.ZERO): SourceCodeBlock = document.merge(s, pos)

  /** Return the result document */
  override def astResult: SourceCodeBlock = document

  override def parsedDocument: ParsedDocument = StringParsedDocument(document)

  private def sourceCodeBlock(
      indentation: Integer = document.indentation,
      lines: mutable.Buffer[(String, Position)] = mutable.Buffer()
  ): SourceCodeBlock =
    new SourceCodeBlock(indentation, lines)

  private def stringDocBuilder(
      indentation: Integer = document.indentation,
      lines: mutable.Buffer[(String, Position)] = mutable.Buffer(),
      f: StringDocBuilder => Unit,
      toString: SourceCodeBlock => String = _.builder.toString().dropRight(1)
  ): StringDocBuilder = {
    val scb              = sourceCodeBlock(indentation, lines)
    val stringDocBuilder = new StringDocBuilder(scb)
    f(stringDocBuilder)
    val s   = toString(scb)
    val pos = scb.minPos
    merge(s, pos)
    this
  }
}
