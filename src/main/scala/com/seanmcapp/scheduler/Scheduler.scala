package com.seanmcapp.scheduler

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.Materializer
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder
import org.joda.time.{DateTime, DateTimeZone, LocalDateTime}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration}

abstract class Scheduler(startTime: Int, intervalOpt: Option[FiniteDuration])
                        (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) {
  // TODO: Add tests for all scheduler
  private val ICT = "+07:00"
  protected def now: DateTime = new DateTime().toDateTime(DateTimeZone.forID(ICT))
  protected val scheduler = system.scheduler

  def run: Cancellable = {
    val startTimeDuration = getStartTimeDuration(startTime)
    intervalOpt match {
      case Some(interval) =>
        scheduler.schedule(startTimeDuration, interval)(task)
      case None => scheduler.scheduleOnce(startTimeDuration)(task)
    }
  }

  protected def getStartTimeDuration(hour: Int): FiniteDuration = {
    val init = new LocalDateTime()
      .withTime(hour,0,0,0)
      .toDateTime(DateTimeZone.forID(ICT))
    val target = if (now.getHourOfDay >= hour) init.plusDays(1) else init
    val numberInMillis = target.getMillis - now.getMillis
    Duration(numberInMillis, TimeUnit.MILLISECONDS)
  }

  protected def task: Any

}
