package com.seanmcapp.scheduler

import com.seanmcapp.SchedulerForTest
import com.seanmcapp.mock.requestbuilder.TelegramRequestBuilderMock
import com.seanmcapp.util.parser.decoder.AirvisualCity
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._

import scala.io.Source

class AirVisualSchedulerSpec extends AsyncWordSpec with Matchers with SchedulerForTest {

  "AirVisualScheduler should return correctly" in {
    val airVisual = new AirVisualScheduler(startTime, interval, http) with TelegramRequestBuilderMock
    val mockResponse = Source.fromResource("scheduler/airvisual_response.json").mkString
    when(http.sendGetRequest(anyString())).thenReturn(mockResponse)
    val expected = Map(
      AirvisualCity("Indonesia", "Jakarta", "Jakarta") -> 119,
      AirvisualCity("Indonesia", "West Java", "Bekasi") -> 119,
      AirvisualCity("Indonesia", "West Java", "Depok") -> 119,
      AirvisualCity("Singapore", "Singapore", "Singapore") -> 119,
      AirvisualCity("Indonesia", "Riau", "Pekanbaru") -> 119,
      AirvisualCity("Indonesia", "Central Kalimantan", "Palangkaraya") -> 119
    )
    airVisual.task shouldBe expected
  }

}
