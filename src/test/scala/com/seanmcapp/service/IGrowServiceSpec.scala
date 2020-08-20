package com.seanmcapp.service

import com.seanmcapp.config.SchedulerConf
import com.seanmcapp.external.{IGrowClient, IgrowData, IgrowResponse, TelegramClient, TelegramResponse}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class IGrowServiceSpec extends AnyWordSpec with Matchers {

  "Scheduler" in {
    val igrowClient = Mockito.mock(classOf[IGrowClient])
    val telegramClient = Mockito.mock(classOf[TelegramClient])
    val telegramResponse = Mockito.mock(classOf[TelegramResponse])
    when(telegramClient.sendMessage(any(), any())).thenReturn(telegramResponse)
    val mockResponse = IgrowResponse(List(
      IgrowData("Gemilang Sarea Farm Eggs Project", 4000000, 26, "18% per annum", 3),
      IgrowData("Tilapia Sumber Nila Berkah", 4800000, 0, "18% per annum", 2),
      IgrowData("Goldfish Mayang Mas Sejahtera", 5000000, 0, "18% per annum", 2),
      IgrowData("Chicken Eggs Olat Maras Farm", 4991000, 0, "13% per annum", 3),
      IgrowData("eFishery Tech Village : Catfish", 5000000, 0, "14% per annum", 2),
      IgrowData("Corn in Dompu", 6200000, 0, "16 % per annum",2)
    ))
    when(igrowClient.getList).thenReturn(mockResponse)
    val igrowService = new IGrowService(igrowClient, telegramClient) {
      override private[service] val schedulerConf = SchedulerConf(Seq(1L))
    }
    val expected = List("Gemilang Sarea Farm Eggs Project%0A4000000%0Asisa 26 unit")
    igrowService.run shouldBe expected
  }

}
