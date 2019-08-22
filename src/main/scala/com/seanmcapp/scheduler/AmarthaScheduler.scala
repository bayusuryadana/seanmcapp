package com.seanmcapp.scheduler

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.config.{AmarthaConf, SchedulerConf}
import com.seanmcapp.util.cache.MemoryCache
import com.seanmcapp.util.parser.decoder.{AmarthaAuthData, AmarthaDecoder, AmarthaMarketplaceData, AmarthaMarketplaceItem, AmarthaResponse}
import com.seanmcapp.util.requestbuilder.{HttpRequestBuilder, TelegramRequestBuilder}
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._

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
      val authData = decode[AmarthaAuthData](authResponse.data)
      println("account: " + authData.name)
      val url = amarthaBaseUrl + "/marketplace"
      val headers = Some(Map("x-access-token" -> authData.accessToken))
      val timeout = Some((15000, 300000))
      val httpResponse = http.sendRequest(url, headers = headers, timeout = timeout)
      val amarthaResponse = decode[AmarthaResponse](httpResponse)
      val response = decode[AmarthaMarketplaceData](amarthaResponse.data)
      // TODO: (for Analytics)
      println(response.marketplace.size)
      response.marketplace.foreach(item => println(s"${item.borrowerName}(${item.creditScoreGrade}): ${item.plafond}"))

      val stringMessage = "Amartha: " + response.marketplace.size + " orang perlu didanai " + "(" + startTime + ":00)"
      val schedulerConf = SchedulerConf()
      schedulerConf.amartha.foreach(chatId => sendMessage(chatId, stringMessage))
      rerun(response)
      response.marketplace
    } else throw new Exception(authResponse.toString)
  }

  private def auth: AmarthaResponse = {
    val amarthaConf = AmarthaConf()
    val url = amarthaBaseUrl + "/auth"
    val postData = Some(s"""{"username": "${amarthaConf.username}","password": "${amarthaConf.password}"}""")
    val headers = Some(Map("Content-Type" -> "application/json"))
    val timeout = Some((15000, 300000))
    val response = http.sendRequest(url, postData, headers, timeout)
    println(response)
    decode[AmarthaResponse](response)
  }

  private[scheduler] def rerun(response: AmarthaMarketplaceData): Unit = {
    if (response.marketplace.nonEmpty) new AmarthaScheduler(startTime + 1, None, http).run
  }

}
