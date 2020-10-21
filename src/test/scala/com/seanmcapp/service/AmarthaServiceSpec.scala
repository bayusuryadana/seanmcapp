package com.seanmcapp.service

import java.text.NumberFormat

import com.seanmcapp.external.{AmarthaAuthData, AmarthaClient, AmarthaDetail, AmarthaInstallment, AmarthaMitraIdList, AmarthaMitraView, AmarthaPortofolio, AmarthaSummary, AmarthaSummaryDetail, AmarthaTransaction, AmarthaView, TelegramClient, TelegramResponse}
import com.seanmcapp.util.MonthUtil
import org.joda.time.DateTime
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.SortedMap

class AmarthaServiceSpec extends AnyWordSpec with Matchers {

  val amarthaClient = Mockito.mock(classOf[AmarthaClient])
  val telegramClient = Mockito.mock(classOf[TelegramClient])
  val amarthaAuthData = AmarthaAuthData("token", true, "", "Ismurroozi")
  when(amarthaClient.getTokenAuth(any(), any())).thenReturn(amarthaAuthData)
  val amarthaService = new AmarthaService(amarthaClient, telegramClient)

  "getAmarthaView" in {
    val amarthaSummary = AmarthaSummary(Some("pawas"), 1, 1, 1, 1, 1)
    when(amarthaClient.getAllSummary(any())).thenReturn(amarthaSummary)
    val amarthaMitraList = AmarthaMitraIdList(List(
      AmarthaPortofolio(50, "A", "2020-07-30T09:00:00+07:00", true, true, 793917, "ADEM SARI", 4500000, "ONTIME",
        "https://pic1.jpg", "https://pic2.jpg"),
      AmarthaPortofolio(50, "A", "2020-07-30T07:00:00+07:00", true, true, 793142, "NUR AFI ", 4500000, "ONTIME",
        "https://pic1.jpg", "https://pic2.jpg"),
      AmarthaPortofolio(50, "A", "2020-07-30T09:00:00+07:00", true, true, 755750, "EHEK ", 4000000, "ONTIME",
        "https://pic1.jpg", "https://pic2.jpg"),
      AmarthaPortofolio(50, "A", "2020-07-30T09:00:00+07:00", true, true, 759541, "CRYSTAL", 4000000, "ONTIME",
        "https://pic1.jpg", "https://pic2.jpg"),
      AmarthaPortofolio(50, "A", "2020-06-17T07:00:00+07:00", true, true, 753724, "RUBY " , 4000000, "ONTIME",
        "https://pic1.jpg", "https://pic2.jpg")
    ))
    when(amarthaClient.getMitraList(any())).thenReturn(amarthaMitraList)
    val amarthaDetail = AmarthaDetail(List.empty[AmarthaInstallment], AmarthaSummaryDetail(0))
    when(amarthaClient.getMitraDetail(any(), any())).thenReturn(amarthaDetail)
    val result = amarthaService.getAmarthaView()
    def formatter(in: Long): String = NumberFormat.getIntegerInstance.format(in)
    val amarthaMitraViewExpected = amarthaMitraList.portofolio.map { i =>
      i.loanId -> AmarthaMitraView(i.loanId, i.name, "0.0%", 50, formatter(i.plafond),List())
    }.to(SortedMap)
    result shouldBe AmarthaView("1","1","21,000,000",List.empty[String], amarthaMitraViewExpected)
  }

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
