package com.seanmcapp.scheduler

import com.seanmcapp.SchedulerForTest
import com.seanmcapp.mock.requestbuilder.TelegramRequestBuilderMock
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._

class NCovSchedulerSpec extends AsyncWordSpec with Matchers with SchedulerForTest {

  "NCovScheduler should return correctly" in {
    val nCovScheduler = new NCovScheduler(startTime, interval, http) with TelegramRequestBuilderMock
    when(http.sendGetRequest(anyString())).thenReturn("Singapore/Indonesia,50")
    nCovScheduler.task shouldBe "Singapore case Confirmed: 50, Death: 50, Recovered: 50\nIndonesia case Confirmed: 50, Death: 50, Recovered: 50"
  }

}
