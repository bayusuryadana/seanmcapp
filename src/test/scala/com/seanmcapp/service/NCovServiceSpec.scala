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
    val mockResponse = List("Singapore/Indonesia,90,100", "Singapore/Indonesia,8,10", "Singapore/Indonesia,8,10")
    when(ncovClient.getReport).thenReturn(mockResponse)
    val expected =
      s"""Singapore total reported cases: 100
         |today's new cases: 10
         |death cases: 10
         |recovered cases: 10
         |active case: 80
         |
         |Indonesia total reported cases: 100
         |today's new cases: 10
         |death cases: 10
         |recovered cases: 10
         |active case: 80
         |""".stripMargin
    ncovService.run shouldBe expected
  }

}
