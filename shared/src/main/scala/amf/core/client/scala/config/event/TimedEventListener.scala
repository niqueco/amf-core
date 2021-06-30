package amf.core.client.scala.config.event

import amf.core.client.scala.config.{AMFEvent, AMFEventListener}

case class TimedEvent(timeInMillis: Long, timed: AMFEvent)

case class TimedEventListener(time: () => Long, downstream: TimedEvent => Unit) extends AMFEventListener {
  override def notifyEvent(event: AMFEvent): Unit = {
    downstream(TimedEvent(time(), event))
  }
}
