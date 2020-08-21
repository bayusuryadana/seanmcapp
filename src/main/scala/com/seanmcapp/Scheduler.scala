package com.seanmcapp

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Cancellable}
import com.seanmcapp.service.ScheduledTask
import org.joda.time.{DateTime, DateTimeZone, LocalDateTime}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration}

class Scheduler(startTime: Int, intervalOpt: Option[FiniteDuration], scheduledTask: ScheduledTask)
                        (implicit system: ActorSystem) extends Runnable {
  private[seanmcapp] val ICT = "+07:00"
  protected def now: DateTime = new DateTime().toDateTime(DateTimeZone.forID(ICT))
  protected val scheduler = system.scheduler
  private implicit val _ec: ExecutionContext = system.dispatcher

  // $COVERAGE-OFF$
  def start: Cancellable = {
    val startTimeDuration = getStartTimeDuration(startTime)
    intervalOpt match {
      case Some(interval) =>
        scheduler.scheduleAtFixedRate(startTimeDuration, interval)(this)
      case None => scheduler.scheduleOnce(startTimeDuration)(this.run())
    }
  }

  override def run: Unit = scheduledTask.run
  // $COVERAGE-ON$

  private[seanmcapp] def getStartTimeDuration(hour: Int): FiniteDuration = {
    if (hour < 0) {
      Duration(startTime, TimeUnit.SECONDS)
    } else {
      val init = new LocalDateTime()
        .withTime(hour, 0, 0, 0)
        .toDateTime(DateTimeZone.forID(ICT))
      val target = if (now.getHourOfDay >= hour) init.plusDays(1) else init
      val numberInMillis = target.getMillis - now.getMillis
      Duration(numberInMillis, TimeUnit.MILLISECONDS)
    }
  }

}

