package com.seanmcapp.fetcher

import com.seanmcapp.util.parser.decoder.AirvisualCity
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future
import scala.io.Source

class AirVisualFetcherSpec extends AsyncWordSpec with Matchers {

  "AirVisualFetcher should fetch correctly" in {
    val http = Mockito.mock(classOf[HttpRequestClient])
    val airVisualFetcher = new AirVisualFetcher(http)
    val mockResponse = Source.fromResource("scheduler/airvisual_response.json").mkString
    when(http.sendGetRequest(any())).thenReturn(Future.successful(Right(mockResponse)))
    val expected = Map(
      AirvisualCity("Indonesia", "Jakarta", "Jakarta") -> 119,
      AirvisualCity("Indonesia", "West Java", "Bekasi") -> 119,
      AirvisualCity("Indonesia", "West Java", "Depok") -> 119,
      AirvisualCity("Singapore", "Singapore", "Singapore") -> 119,
      AirvisualCity("Indonesia", "Riau", "Pekanbaru") -> 119,
      AirvisualCity("Indonesia", "Central Kalimantan", "Palangkaraya") -> 119
    )

    airVisualFetcher.getCityResults().map { res =>
      res shouldBe expected
    }
  }

}
