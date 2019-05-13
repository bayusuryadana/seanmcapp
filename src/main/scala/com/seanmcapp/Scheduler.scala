package com.seanmcapp

import java.util.concurrent.TimeUnit

import org.joda.time.{DateTime, DateTimeZone, LocalDateTime}
import com.seanmcapp.Boot.system
import com.seanmcapp.repository.birthday.PeopleRepoImpl
import com.seanmcapp.util.parser.{IgrowData, IgrowResponse}
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder
import scalaj.http.Http
import spray.json._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

object Scheduler extends TelegramRequestBuilder {

  private val peopleRepo = PeopleRepoImpl
  private val ICT = "+07:00"
  private val iGrowBaseUrl = "https://igrow.asia/api/public/en/v1/sponsor/seed"

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
    birthdayCheck
    iGrowCheck
    println("=== fetching news here ===")
  }

  private def birthdayCheck: Future[String] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    println("=== birthday check ===")
    val now = DateTime.now // akka datetime doesn't support timezones, so this is UTC
    for{
      people <- peopleRepo.get(now.getDayOfMonth, now.getMonthOfYear)
    } yield {
      val result = "Today's birthday: " + people.map(_.name + ",")
      people.map(person => sendMessage(274852283, "Today is " + person.name + " birthday !!"))
      result
    }
  }

  private def iGrowCheck: Seq[IgrowData] = {
    import com.seanmcapp.util.parser.IgrowJson._
    val response = Http(iGrowBaseUrl + "/list").asString.body.parseJson.convertTo[IgrowResponse].data.filter(_.stock > 0)
    response.map(data => sendMessage(274852283, "ada stok " + data.name + " sisa " + data.stock + " unit%0A%40"))
    response
  }

  private def now: DateTime = new DateTime().toDateTime(DateTimeZone.forID(ICT))

}
