package com.seanmcapp.util

import com.seanmcapp.service.ScheduledTask
import cron4s._
import cron4s.lib.joda._
import org.apache.pekko.actor.{ActorSystem, Cancellable}
import org.joda.time.{DateTime, DateTimeZone}

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

// $COVERAGE-OFF$
class Scheduler(scheduledTask: ScheduledTask, cronString: String, isRepeat: Boolean = true)
               (implicit system: ActorSystem) extends Runnable {

  private[seanmcapp] val ICT = "+07:00"
  protected def now: DateTime = new DateTime().toDateTime(DateTimeZone.forID(ICT))
  private val scheduler = system.scheduler
  private implicit val _ec: ExecutionContext = system.dispatcher

  def start: Cancellable = {
    val nextSchedule = Cron(cronString).toOption.flatMap(_.next(now).map { target =>
      Duration(target.getMillis - now.getMillis, TimeUnit.MILLISECONDS)
    }).getOrElse(throw new Exception("Invalid cron schedule format"))

    scheduler.scheduleOnce(nextSchedule)(this.run())
  }

  override def run(): Unit = {
    try {
      scheduledTask.run
    } catch {
      case e: Exception => new ExceptionHandler(e).doPrint()
    }
    if (isRepeat) start
  }

}

