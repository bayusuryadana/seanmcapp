package com.seanmcapp.scheduler

import com.seanmcapp.SchedulerForTest
import com.seanmcapp.mock.requestbuilder.TelegramRequestBuilderMock
import com.seanmcapp.util.parser.decoder.IgrowData
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.when
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source

class IGrowSchedulerSpec extends AsyncWordSpec with Matchers with SchedulerForTest {

  "IGrowScheduler should return correctly" in {
    val igrow = new IGrowScheduler(startTime, interval, http) with TelegramRequestBuilderMock
    val mockResponse = Source.fromResource("scheduler/igrow_response.json").mkString
    when(http.sendRequest(anyString(), any(), any(), any())).thenReturn(mockResponse)
    igrow.task shouldBe List(IgrowData("Gemilang Sarea Farm Eggs Project",4000000,26,"18% per annum",3))
  }

}
