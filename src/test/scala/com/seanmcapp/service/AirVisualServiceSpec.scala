package com.seanmcapp.service

import com.seanmcapp.external.{AirVisualClient, AirvisualCity, TelegramClient}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AirVisualServiceSpec extends AnyWordSpec with Matchers {

  "Scheduler" in {
    val airVisualClient = Mockito.mock(classOf[AirVisualClient])
    val telegramClient = Mockito.mock(classOf[TelegramClient])
    val mockResponse = Map(
      AirvisualCity("Indonesia", "Jakarta", "Jakarta") -> 30,
      AirvisualCity("Indonesia", "West Java", "Bekasi") -> 80,
      AirvisualCity("Indonesia", "West Java", "Depok") -> 130,
      AirvisualCity("Singapore", "Singapore", "Singapore") -> 180
    )
    when(airVisualClient.getCityResults).thenReturn(mockResponse)
    val airVisualService = new AirVisualService(airVisualClient, telegramClient)
    val result = airVisualService.run
    val expected = s"""*Seanmcearth* melaporkan kondisi udara saat ini:
                      |Jakarta (AQI 30 🍀)
                      |Bekasi (AQI 80 😎)
                      |Depok (AQI 130 😰)
                      |Singapore (AQI 180 😷)""".stripMargin
    result shouldBe expected
  }

}
