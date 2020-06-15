package com.seanmcapp.scheduler

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.Materializer
import org.joda.time.{DateTime, DateTimeZone, LocalDateTime}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration}

// $COVERAGE-OFF$
abstract class Scheduler(startTime: Int, intervalOpt: Option[FiniteDuration])
                        (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends Runnable {
  private val ICT = "+07:00"
  protected def now: DateTime = new DateTime().toDateTime(DateTimeZone.forID(ICT))
  protected val scheduler = system.scheduler

  def start: Cancellable = {
    val startTimeDuration = getStartTimeDuration(startTime)
    intervalOpt match {
      case Some(interval) =>
        scheduler.scheduleAtFixedRate(startTimeDuration, interval)(this)
      case None => scheduler.scheduleOnce(startTimeDuration)(this.run)
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

  override def run: Unit = task

  protected def task: Any

}
// $COVERAGE-ON$
