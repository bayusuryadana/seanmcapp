package com.seanmcapp.service

import java.util.concurrent.TimeUnit

import com.seanmcapp.util.cache.MemoryCache
import com.seanmcapp.util.parser.decoder.{AmarthaAuthData, AmarthaInputDecoder, AmarthaResponse, AmarthaTransaction}
import com.seanmcapp.util.parser.encoder.{AmarthaAuthPayload, AmarthaOutputEncoder}
import com.seanmcapp.util.requestbuilder.{HeaderMap, HttpRequestBuilder}
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._
import spray.json.{JsValue, JsonReader}

import scala.concurrent.duration.Duration

class AmarthaService(http: HttpRequestBuilder) extends AmarthaInputDecoder with AmarthaOutputEncoder with MemoryCache {

  implicit val tokenCache = createCache[String]
  private val duration = Duration(30, TimeUnit.MINUTES)

  def getAccountInfo(username: String, password: String): String = {
    val url = AmarthaEndpoint.baseUrl + AmarthaEndpoint.account
    send[JsValue](username, password, url).prettyPrint
  }

  def getSummary(username: String, password: String): String = {
    val url = AmarthaEndpoint.baseUrl + AmarthaEndpoint.allSummary
    send[JsValue](username, password, url).prettyPrint
  }

  def getMitraList(username: String, password: String): String = {
    val url = AmarthaEndpoint.baseUrl + AmarthaEndpoint.list
    send[JsValue](username, password, url).prettyPrint
  }

  def getMitraDetails(username: String, password: String, loanId: Int): String = {
    val url = AmarthaEndpoint.baseUrl + AmarthaEndpoint.details + loanId
    send[JsValue](username, password, url).prettyPrint
  }

  def getTransaction(username: String, password: String): String = {
    val url = AmarthaEndpoint.baseUrl + AmarthaEndpoint.transaction
    val transactionList = send[Seq[AmarthaTransaction]](username, password, url)
    transactionList.toString()
  }

  private def send[T:JsonReader](username: String, password: String, url: String): T = {
    val accessToken = getTokenAuth(username, password)
    val headers = HeaderMap(Map(
      "x-access-token" -> accessToken
    ))
    val httpResponse = http.sendRequest(url, headers = Some(headers))
    val dataResponse = decode[AmarthaResponse](httpResponse).data
    decode[T](dataResponse)
  }

  private def getTokenAuth(username: String, password: String): String = {
    memoizeSync(Some(duration)) {
      val amarthaPayload = AmarthaAuthPayload(username, password)
      val payload = encode(amarthaPayload).compactPrint
      val url = AmarthaEndpoint.baseUrl + AmarthaEndpoint.auth
      val headers = HeaderMap(Map(
        "Content-Type" -> "application/json",
        "User-Agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36"
      ))
      val httpResponse = http.sendRequest(url, postData = Some(payload), headers = Some(headers))

      val amarthaResponse = decode[AmarthaResponse](httpResponse)
      val amarthaAuthData = decode[AmarthaAuthData](amarthaResponse.data)

      amarthaAuthData.accessToken
    }
  }
}

object AmarthaEndpoint {
  val baseUrl = "https://dashboard.amartha.com/v2"

  val auth = "/auth"
  val account = "/investor/me"
  val allSummary = "/investor/account"
  val miniSummary = "/account/summary" // useless
  val marketplace = "/marketplace" // not priority
  val list = "/portofolio/list-mitra"
  val details = "/portofolio/detail-mitra?id=" // ${loanId}
  val transaction = "/account/transaction"
}

