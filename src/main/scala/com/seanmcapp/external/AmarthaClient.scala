package com.seanmcapp.external

import java.util.concurrent.TimeUnit

import com.seanmcapp.util.MemoryCache
import io.circe.Printer
import io.circe.syntax._
import scalacache.Cache
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._

import scala.concurrent.duration.Duration

class AmarthaClient(http: HttpRequestClient) extends MemoryCache {

  implicit val tokenCache: Cache[AmarthaAuthData] = createCache[AmarthaAuthData]
  implicit val transactionCache: Cache[List[AmarthaTransaction]] = createCache[List[AmarthaTransaction]]

  private val duration = Duration(30, TimeUnit.MINUTES)

  import AmarthaEndpoint._

  def getTransaction(accessToken: String): List[AmarthaTransaction] = {
    memoizeSync(Some(duration)) {
      val headers = HeaderMap(Map(
        "x-access-token" -> accessToken
      ))
      val httpResponse = http.sendGetRequest(baseUrl + transaction, headers = Some(headers))
      decode[AmarthaResponse[List[AmarthaTransaction]]](httpResponse).data
    }
  }

  def getTokenAuth(username: String, password: String): AmarthaAuthData = {
    memoizeSync(Some(duration)) {
      val amarthaPayload = AmarthaAuthPayload(username, password)
      val payload = amarthaPayload.asJson.printWith(Printer.noSpacesSortKeys)
      val url = baseUrl + auth
      val headers = HeaderMap(Map(
        "Content-Type" -> "application/json",
        "User-Agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36"
      ))
      val httpResponse = http.sendRequest(url, postData = Some(payload), headers = Some(headers))

      decode[AmarthaResponse[AmarthaAuthData]](httpResponse.body).data
    }
  }
}

object AmarthaEndpoint {
  val baseUrl = "https://dashboard.amartha.com/v2"

  val auth = "/auth"
  val transaction = "/account/transaction"
}