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

  val amarthaClient = Mockito.mock(classOf[AmarthaClient])
  val telegramClient = Mockito.mock(classOf[TelegramClient])
  val amarthaAuthData = AmarthaAuthData("token", true, "", "Ismurroozi")
  when(amarthaClient.getTokenAuth(any(), any())).thenReturn(amarthaAuthData)
  val amarthaService = new AmarthaService(amarthaClient, telegramClient)

  "Scheduler" in {
    val dateTimeSplit = DateTime.now().toString("dd MM YYYY").split(" ")
    val monthMap = MonthUtil.map.toList.map { case (key, value) => value -> key}.toMap
    val resultDate = s"${dateTimeSplit(0)} ${monthMap(dateTimeSplit(1))} ${dateTimeSplit(2)}"
    val amarthaTransaction = List(
      AmarthaTransaction("0", resultDate, "10.000", "Imbal Hasil", "17.744.750"),
      AmarthaTransaction("0", resultDate, "0", "Imbal Hasil", "17.744.750")
    )
    when(amarthaClient.getTransaction(any())).thenReturn(amarthaTransaction)
    val telegramResponse = Mockito.mock(classOf[TelegramResponse])
    when(telegramClient.sendMessage(any(), any())).thenReturn(telegramResponse)
    val result = amarthaService.run
    val expected = s"""[Amartha]
                      |Today's revenue: Rp. 10,000
                      |Current balance: Rp. 17.744.750
                      |Paid percentage: 50%""".stripMargin
    result shouldBe expected
  }

}
