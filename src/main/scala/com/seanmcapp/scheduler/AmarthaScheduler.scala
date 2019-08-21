package com.seanmcapp.scheduler

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.config.{AmarthaConf, SchedulerConf}
import com.seanmcapp.util.cache.MemoryCache
import com.seanmcapp.util.parser.{AmarthaAuthData, AmarthaDecoder, AmarthaMarketplaceData, AmarthaMarketplaceItem, AmarthaResponse}
import com.seanmcapp.util.requestbuilder.{HttpRequestBuilder, TelegramRequestBuilder}
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._
import scalaj.http.Http
import spray.json._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration}

class AmarthaScheduler(startTime: Int, interval: Option[FiniteDuration], override val http: HttpRequestBuilder)
                      (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, interval) with AmarthaDecoder with TelegramRequestBuilder with MemoryCache {

  private val amarthaBaseUrl = "https://dashboard.amartha.com/v2"
  private val duration = Duration(15, TimeUnit.MINUTES)
  implicit val amarthaCache = createCache[AmarthaResponse]

  override def task: Seq[AmarthaMarketplaceItem] = {
    println(" === amartha check ===")
    val authResponse: AmarthaResponse = memoizeSync(Some(duration))(auth)
    if (authResponse.code == 200) {
      val authData = authResponse.data.convertTo[AmarthaAuthData]
      println("account: " + authData.name)
      val response = Http(amarthaBaseUrl + "/marketplace")
        .header("x-access-token", authData.accessToken)
        .timeout(15000, 300000)
        .asString.body.parseJson.convertTo[AmarthaResponse].data.convertTo[AmarthaMarketplaceData]
      // TODO: logging lenderId
      println(response.marketplace.size)
      response.marketplace.foreach(item => println(s"${item.borrowerName}(${item.creditScoreGrade}): ${item.plafond}"))

      val stringMessage = "Amartha: " + response.marketplace.size + " orang perlu didanai " + "(" + startTime + ":00)"
      val schedulerConf = SchedulerConf()
      schedulerConf.amartha.foreach(chatId => sendMessage(chatId, stringMessage))
      if (response.marketplace.nonEmpty) new AmarthaScheduler(startTime + 1, None, http).run
      response.marketplace
    } else throw new Exception(authResponse.toString)
  }

  private def auth: AmarthaResponse = {
    val amarthaConf = AmarthaConf()
    Http(amarthaBaseUrl + "/auth")
      .postData(s"""{"username": "${amarthaConf.username}","password": "${amarthaConf.password}"}""")
      .header("Content-Type", "application/json")
      .timeout(15000, 300000)
      .asString.body.parseJson.convertTo[AmarthaResponse]
  }

}
