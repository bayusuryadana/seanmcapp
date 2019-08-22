package com.seanmcapp.scheduler

import com.seanmcapp.SchedulerForTest
import com.seanmcapp.mock.requestbuilder.TelegramRequestBuilderMock
import com.seanmcapp.util.parser.decoder.{AmarthaMarketplaceData, AmarthaMarketplaceItem}
import org.mockito.Mockito.when
import org.scalatest.{AsyncWordSpec, Matchers}

import scala.io.Source

class AmarthaSchedulerSpec extends AsyncWordSpec with Matchers with SchedulerForTest {

  private val amarthaBaseUrl = "https://dashboard.amartha.com/v2"

  "AmarthaScheduler should return correctly" in {
    val amartha = new AmarthaScheduler(startTime, None, http) with TelegramRequestBuilderMock {
      override def rerun(response: AmarthaMarketplaceData): Unit = {}
    }
    val authResponse = Source.fromResource("scheduler/amartha/auth_response.json").mkString
    val postData = Some(s"""{"username": "","password": ""}""")
    val authHeaders = Some(Map("Content-Type" -> "application/json"))
    val timeout = Some((15000, 300000))
    when(http.sendRequest(amarthaBaseUrl + "/auth", postData, authHeaders, timeout)).thenReturn(authResponse)

    val headers = Some(Map("x-access-token" -> "access_token"))
    val marketplaceResponse = Source.fromResource("scheduler/amartha/marketplace_response.json").mkString
   when(http.sendRequest(amarthaBaseUrl + "/marketplace", headers = headers, timeout = timeout)).thenReturn(marketplaceResponse)

    amartha.task shouldBe List(AmarthaMarketplaceItem("Kamisah", "A-", 2500000, 325000, 50, "Modal Dagang Warung Makan"))
  }

}
