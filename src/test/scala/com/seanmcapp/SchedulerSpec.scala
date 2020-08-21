package com.seanmcapp

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import com.seanmcapp.service.ScheduledTask
import org.joda.time.{DateTime, DateTimeZone, LocalDateTime}
import org.mockito.Mockito
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.Duration

class SchedulerSpec extends AnyWordSpec with Matchers {

  "getStartTimeDuration" in {
    implicit val actor = Mockito.mock(classOf[ActorSystem])
    val task = new ScheduledTask {
      override def run: Any = "do something"
    }
    val scheduler = new Scheduler(1, Some(Duration(1, TimeUnit.MINUTES)), task)
    val result1 = scheduler.getStartTimeDuration(-1)

    val now = new DateTime().toDateTime(DateTimeZone.forID(scheduler.ICT))
    val target = now.plusHours(1)

    val minutes = target.getMinuteOfHour
    val numberInMillis = Duration(target.minusMinutes(minutes+1).getMillis - now.getMillis, TimeUnit.MILLISECONDS).toMinutes
    val result2 = Duration(scheduler.getStartTimeDuration(target.getHourOfDay).toMinutes, TimeUnit.MINUTES)

    result1 shouldBe Duration(1, TimeUnit.SECONDS)
    result2 shouldBe Duration(numberInMillis, TimeUnit.MINUTES)

  }

}
