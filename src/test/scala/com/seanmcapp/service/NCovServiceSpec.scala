package com.seanmcapp.service

import com.seanmcapp.external.{NCovClient, TelegramClient}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class NCovServiceSpec extends AnyWordSpec with Matchers {

  "Scheduler" in {
    val ncovClient = Mockito.mock(classOf[NCovClient])
    val telegramClient = Mockito.mock(classOf[TelegramClient])
    val ncovService = new NCovService(ncovClient, telegramClient)
    val mockResponse = List("Singapore/Indonesia,100", "Singapore/Indonesia,10", "Singapore/Indonesia,10")
    when(ncovClient.getReport).thenReturn(mockResponse)
    val expected = s"Singapore new case: 100, active case: 80\nIndonesia new case: 100, active case: 80"
    ncovService.run shouldBe expected
  }

}
