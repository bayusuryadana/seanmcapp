package com.seanmcapp.service

import com.seanmcapp.external.{DsdaJakartaClient, TelegramClient, TelegramResponse}
import com.seanmcapp.util.{DsdaWaterGate, DsdaWaterGateResponse}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DsdaJakartaServiceSpec extends AnyWordSpec with Matchers {

  "Scheduler" in {
    val dsdaClient = Mockito.mock(classOf[DsdaJakartaClient])
    val telegramClient = Mockito.mock(classOf[TelegramClient])

    val dsdaResponse = DsdaWaterGateResponse(List(
      DsdaWaterGate("Manggarai", "Status : Siaga 3"),
      DsdaWaterGate("Bekasi", "Status : Normal"),
      DsdaWaterGate("Depok", "Status : Siaga 2")
    ))
    when(dsdaClient.getReport).thenReturn(dsdaResponse)
    val telegramResponse = Mockito.mock(classOf[TelegramResponse])
    when(telegramClient.sendMessage(any(), any())).thenReturn(telegramResponse)
    val dsda = new DsdaJakartaService(dsdaClient, telegramClient)
    val expected =
      s"""Seanmcapp melaporkan pintu air siaga:
         |
         |Manggarai: Siaga 3
         |Depok: Siaga 2""".stripMargin
    dsda.run shouldBe expected
  }

}
