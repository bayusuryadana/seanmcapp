package com.seanmcapp

import java.util.concurrent.TimeUnit

import org.joda.time.{DateTime, DateTimeZone, LocalDateTime}
import com.seanmcapp.Boot.system
import com.seanmcapp.repository.birthday.PeopleRepoImpl
import com.seanmcapp.service.BirthdayService


import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

object Scheduler extends BirthdayService {

  override val peopleRepo = PeopleRepoImpl

  val ICT = "+07:00"

  def start(implicit ec: ExecutionContext): Unit = {
    val scheduler = system.scheduler

    // one-time warmup DB
    scheduler.scheduleOnce(Duration(0, TimeUnit.SECONDS))(warmup)
    scheduler.scheduleOnce(Duration(10, TimeUnit.SECONDS))(warmup)

    // scheduler for everyday at 6 AM (GMT+7)
    val init = new LocalDateTime()
      .withTime(6,0,0,0)
      .toDateTime(DateTimeZone.forID(ICT))
    val target = if (now.getHourOfDay >= 6) init.plusDays(1) else init
    val numberInMillis = target.getMillis - now.getMillis
    scheduler.schedule(Duration(numberInMillis, TimeUnit.MILLISECONDS), Duration(1, TimeUnit.DAYS))(task)
  }

  private def warmup: Unit = {
    println("=== warmup database ===")
    val res = Await.result(peopleRepo.get(now.getDayOfMonth, now.getMonthOfYear), Duration.Inf)
    println("warmup result: " + res)
  }

  private def task: Unit = {
    println("=== checking today's birthday ===")
    birthdayCheck

    println("=== fetching news ===")

  }

  private def now: DateTime = new DateTime().toDateTime(DateTimeZone.forID(ICT))

}
