package com.seanmcapp

import java.util.concurrent.TimeUnit

import org.joda.time.{DateTime, DateTimeZone, LocalDateTime}
import com.seanmcapp.Boot.system
import com.seanmcapp.repository.birthday.PeopleRepoImpl
import com.seanmcapp.repository.dota.{Player, PlayerRepoImpl}
import com.seanmcapp.util.parser.{IgrowData, IgrowResponse, PlayerResponse}
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder
import scalaj.http.Http
import spray.json._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

object Scheduler extends TelegramRequestBuilder {

  private val peopleRepo = PeopleRepoImpl
  private val playerRepo = PlayerRepoImpl

  private val ICT = "+07:00"
  private val iGrowBaseUrl = "https://igrow.asia/api/public/en/v1/sponsor/seed"
  private val dotaBaseUrl = "https://api.opendota.com/api/players/"

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
    dotaMetadataFetcher
    println("=== fetching news here ===")
  }

  private def birthdayCheck: Future[String] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    println("=== birthday check ===")
    for{
      people <- peopleRepo.get(now.getDayOfMonth, now.getMonthOfYear)
    } yield {
      val result = "Today's birthday: " + people.map(_.name + ",")
      people.map(person => sendMessage(274852283, "Today is " + person.name + "'s birthday !!"))
      result
    }
  }

  private def iGrowCheck: Seq[IgrowData] = {
    println("=== iGrow check ===")
    import com.seanmcapp.util.parser.IgrowJson._
    val response = Http(iGrowBaseUrl + "/list").asString.body.parseJson.convertTo[IgrowResponse].data.filter(_.stock > 0)
    val stringMessage = response.foldLeft("") { (res, data) =>
      res + "ada stok " + data.name + " sisa " + data.stock + " unit%0A"
    }
    sendMessage(274852283, stringMessage)
    response
  }

  private def dotaMetadataFetcher: Future[Seq[PlayerResponse]] = {
    println("=== dota metadata fetching ===")
    import com.seanmcapp.util.parser.DotaInputJson._
    import scala.concurrent.ExecutionContext.Implicits.global

    for {
      players <- playerRepo.getAll
    } yield {
      players.map { player =>
        val playerResult = Http(dotaBaseUrl + player.id).asString.body.parseJson.convertTo[PlayerResponse]
        val playerModel = Player(player.id, player.realName,
          playerResult.profile.avatarfull, playerResult.profile.personaName, playerResult.rankTier)
        playerRepo.update(playerModel)
        playerResult
      }
    }
  }

  private def now: DateTime = new DateTime().toDateTime(DateTimeZone.forID(ICT))

}
