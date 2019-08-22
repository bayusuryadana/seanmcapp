package com.seanmcapp.scheduler

import com.seanmcapp.SchedulerForTest
import com.seanmcapp.mock.requestbuilder.TelegramRequestBuilderMock
import com.seanmcapp.util.parser.decoder.{AmarthaMarketplaceData, AmarthaMarketplaceItem}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.{AsyncWordSpec, Matchers}

import scala.io.Source

class AmarthaSchedulerSpec extends AsyncWordSpec with Matchers with SchedulerForTest {

  private val amarthaBaseUrl = "https://dashboard.amartha.com/v2"

  "AmarthaScheduler should return correctly" in {
    val amartha = new AmarthaScheduler(startTime, None, http) with TelegramRequestBuilderMock {
      override def rerun(response: AmarthaMarketplaceData): Unit = {}
    }

    val authResponse = Source.fromResource("scheduler/amartha/auth_response.json").mkString
    when(http.sendRequest(ArgumentMatchers.eq(amarthaBaseUrl + "/auth"), any(), any(), any())).thenReturn(authResponse)

    val marketplaceResponse = Source.fromResource("scheduler/amartha/marketplace_response.json").mkString
    when(http.sendRequest(ArgumentMatchers.eq(amarthaBaseUrl + "/marketplace"), any(), any(), any())).thenReturn(marketplaceResponse)

    amartha.task shouldBe List(AmarthaMarketplaceItem("Kamisah", "A-", 2500000, 325000, 50, "Modal Dagang Warung Makan"))
  }

}
