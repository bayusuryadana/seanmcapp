package com.seanmcapp.service

import java.util.concurrent.TimeUnit

import com.seanmcapp.util.cache.MemoryCache
import com.seanmcapp.util.parser._
import com.seanmcapp.util.requestbuilder.{HeaderMap, HttpRequestBuilder}
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._
import spray.json.JsonReader

import scala.concurrent.duration.Duration
import scala.util.Try

class AmarthaService(http: HttpRequestBuilder) extends AmarthaCommon with MemoryCache {

  implicit val tokenCache = createCache[AmarthaAuthData]
  private val duration = Duration(30, TimeUnit.MINUTES)
  import AmarthaEndpoint._

  def getAmarthaResult(username: String, password: String): String = {
    val result = processResult(username, password)
    encode(result).compactPrint
  }

  def processResult(username: String, password: String): AmarthaResult = {
    val urlSummary = baseUrl + allSummary
    val authData = getTokenAuth(username, password)
    val accessToken = authData.accessToken
    val summary = send[AmarthaSummary](accessToken, urlSummary).copy(namaInvestor = Some(authData.name))

    val urlMitraList = baseUrl + listMitra
    val mitraList = send[AmarthaMitraIdList](accessToken, urlMitraList).portofolio.map { amarthaPortofolio =>
      val urlDetail = baseUrl + details + amarthaPortofolio.loanId
      val amarthaDetail = send[AmarthaDetail](accessToken, urlDetail)
      amarthaPortofolio.copy(
        area = Some(amarthaDetail.loan.areaName),
        branchName = Some(amarthaDetail.loan.branchName),
        dueDate = Some(amarthaDetail.loan.dueDate),
        installment = Some(amarthaDetail.installment),
        provinceName = Some(amarthaDetail.loan.provinceName),
        scheduleDay = Some(amarthaDetail.loan.scheduleDay),
        sector = Some(amarthaDetail.loan.sector),
      )
    }
    val mitraIdNameMap = mitraList.map { amarthaPortfolio =>
      amarthaPortfolio.loanId -> amarthaPortfolio.name
    }.toMap
    val urlTransaction = baseUrl + transaction
    val transactionList = send[List[AmarthaTransaction]](accessToken, urlTransaction).map { amarthaTransaction =>
      val idOpt = Try(amarthaTransaction.loanId.toLong).toOption
      val borrowerNameOpt = idOpt.flatMap(id => mitraIdNameMap.get(id))
      amarthaTransaction.copy(borrowerName = borrowerNameOpt)
    }

    AmarthaResult(summary, mitraList, transactionList)
  }

  private def send[T:JsonReader](accessToken: String, url: String): T = {
    val headers = HeaderMap(Map(
      "x-access-token" -> accessToken
    ))
    val httpResponse = http.sendRequest(url, headers = Some(headers))
    val dataResponse = decode[AmarthaResponse](httpResponse).data
    decode[T](dataResponse)
  }

  private def getTokenAuth(username: String, password: String): AmarthaAuthData = {
    memoizeSync(Some(duration)) {
      val amarthaPayload = AmarthaAuthPayload(username, password)
      val payload = encode(amarthaPayload).compactPrint
      val url = baseUrl + auth
      val headers = HeaderMap(Map(
        "Content-Type" -> "application/json",
        "User-Agent" -> "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36"
      ))
      val httpResponse = http.sendRequest(url, postData = Some(payload), headers = Some(headers))

      val amarthaResponse = decode[AmarthaResponse](httpResponse)
      val amarthaAuthData = decode[AmarthaAuthData](amarthaResponse.data)

      amarthaAuthData
    }
  }
}

object AmarthaEndpoint {
  val baseUrl = "https://dashboard.amartha.com/v2"

  val auth = "/auth"
  // account = "/investor/me"
  val allSummary = "/investor/account"
  // miniSummary = "/account/summary"
  val marketplace = "/marketplace" // not priority
  val listMitra = "/portofolio/list-mitra"
  val details = "/portofolio/detail-mitra?id=" // ${loanId}
  val transaction = "/account/transaction"
}
