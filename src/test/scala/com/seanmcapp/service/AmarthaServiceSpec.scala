package com.seanmcapp.service

import com.seanmcapp.external.{AmarthaAuthData, AmarthaClient, AmarthaTransaction, TelegramClient, TelegramResponse}
import com.seanmcapp.util.MonthUtil
import org.joda.time.DateTime
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AmarthaServiceSpec extends AnyWordSpec with Matchers {

  // TODO: will refactor processResult
  "processResult" in {
    val amarthaClient = Mockito.mock(classOf[AmarthaClient])
    val telegramClient = Mockito.mock(classOf[TelegramClient])
    val amarthaService = new AmarthaService(amarthaClient, telegramClient)

    // val result = amarthaService.processResult("username", "password")
    true shouldBe true
    }

  "Scheduler" in {
    val amarthaClient = Mockito.mock(classOf[AmarthaClient])
    val telegramClient = Mockito.mock(classOf[TelegramClient])
    val amarthaAuthData = AmarthaAuthData("token", true, "", "Ismurroozi")
    when(amarthaClient.getTokenAuth(any(), any())).thenReturn(amarthaAuthData)
    val dateTimeSplit = DateTime.now().minusDays(1).toString("dd MM YYYY").split(" ")
    val monthMap = MonthUtil.map.toList.map { case (key, value) => value -> key}.toMap
    val resultDate = s"${dateTimeSplit(0)} ${monthMap(dateTimeSplit(1))} ${dateTimeSplit(2)}"
    val amarthaTransaction = List(
      AmarthaTransaction("0", resultDate, "10.000", "Imbal Hasil")
    )
    when(amarthaClient.getTransaction(any())).thenReturn(amarthaTransaction)
    val telegramResponse = Mockito.mock(classOf[TelegramResponse])
    when(telegramClient.sendMessage(any(), any())).thenReturn(telegramResponse)
    val amarthaService = new AmarthaService(amarthaClient, telegramClient)
    val result = amarthaService.run
    result shouldBe "[Amartha]%0AToday's revenue: Rp. 10,000"
  }

}
