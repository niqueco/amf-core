package amf.core.client.scala.config.event

import amf.core.client.scala.config.event.AMFEventReport.ANSI_RED
import amf.core.client.scala.config.{AMFEvent, GroupedEvent}

import java.io.StringWriter
import scala.collection.mutable

case class AMFEventReportBuilder private[amf] (private var events: Seq[TimedEvent] = Seq.empty) {

  private val deltaMap = mutable.Map.empty[String, Long]

  def add(event: TimedEvent): Unit = synchronized {
    events ++= Seq(event)
  }

  def build(): AMFEventReport = {
    val startTime: Long = events.headOption.map(_.timeInMillis).getOrElse(0L)
    val eventLogs = events.map { timedEvent =>
      timedEvent.timed match {
        case event: GroupedEvent =>
          val delta: Long = getDeltaWithMostRecentInGroup(timedEvent.timeInMillis, event)
          deltaMap.put(event.groupKey, timedEvent.timeInMillis)
          AMFEventReportLog(timedEvent.timeInMillis - startTime, delta, event.name, event.groupKey)

        case other => AMFEventReportLog(timedEvent.timeInMillis - startTime, 0L, other.name, "")
      }
    }
    val endTime: Long = events.lastOption.map(_.timeInMillis).getOrElse(0L)
    AMFEventReport(startTime, endTime, eventLogs)
  }

  private def getDeltaWithMostRecentInGroup(eventTime: Long, event: AMFEvent with GroupedEvent) = {
    val timeFromLastEventInGroup = deltaMap.get(event.groupKey)
    timeFromLastEventInGroup.map(time => eventTime - time).getOrElse(0L)
  }

  def reset() = {
    events = Seq.empty
    deltaMap.clear()
  }
}

private[amf] object AMFEventReport {
  protected val ANSI_RED = "\u001B[31m"
}

case class AMFEventReport private[amf] (startTime: Long, endTime: Long, logs: Seq[AMFEventReportLog]) {

  override def toString: String = {
    val writer = new StringWriter()
    writer.write(s"---- AMF Run ($totalTime ms) ----\n")
    logs.foreach { log =>
      writer.write(s"${log.toString}\n")
    }
    writer.write(s"(time: $totalTime ms) Execution Finished")
    writer.write("\n\n\n")
    writer.toString
  }

  def print(): Unit = {
    println(s"$ANSI_RED$toString$ANSI_RED")
  }

  def totalTime: Long = endTime - startTime
}

case class AMFEventReportLog private[amf] (time: Long, groupedDelta: Long, eventName: String, groupKey: String) {
  override def toString: String = {
    val formattedKey = if (groupKey.isEmpty) "_" else groupKey
    s"(time: $time ms) (grouped: $groupedDelta ms) name: $eventName / group: $formattedKey"
  }
}
