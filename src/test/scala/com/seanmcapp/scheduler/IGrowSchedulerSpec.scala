package com.seanmcapp.scheduler

import com.seanmcapp.SchedulerForTest
import com.seanmcapp.mock.requestbuilder.TelegramRequestBuilderMock
import com.seanmcapp.util.parser.decoder.IgrowData
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.when
import org.scalatest.{AsyncWordSpec, Matchers}

import scala.io.Source

class IGrowSchedulerSpec extends AsyncWordSpec with Matchers with SchedulerForTest {

  "IGrowScheduler should return correctly" in {
    val igrow = new IGrowScheduler(startTime, interval, http) with TelegramRequestBuilderMock
    val mockResponse = Source.fromResource("scheduler/igrow_response.json").mkString
    when(http.sendRequest(anyString())).thenReturn(mockResponse)
    igrow.task shouldBe List(IgrowData("Gemilang Sarea Farm Eggs Project",4000000,26,"18% per annum",3))
  }

}
