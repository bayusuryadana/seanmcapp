package com.seanmcapp.scheduler

import com.seanmcapp.SchedulerForTest
import com.seanmcapp.mock.requestbuilder.TelegramRequestBuilderMock
import com.seanmcapp.service.AmarthaService
import com.seanmcapp.util.MonthUtil
import com.seanmcapp.util.parser.{AmarthaResult, AmarthaTransaction}
import com.seanmcapp.util.requestbuilder.HttpRequestBuilderImpl
import org.joda.time.DateTime
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class AmarthaSchedulerSpec extends AsyncWordSpec with Matchers with SchedulerForTest {

  "AmarthaScheduler should return correctly" in {
    val amarthaService = Mockito.mock(classOf[AmarthaService])
    val amarthaResult = Mockito.mock(classOf[AmarthaResult])
    val dateTimeSplit = DateTime.now().minusDays(1).toString("dd MM YYYY").split(" ")
    val monthMap = MonthUtil.map.toList.map { case (key, value) => value -> key}.toMap
    val resultDate = s"${dateTimeSplit(0)} ${monthMap(dateTimeSplit(1))} ${dateTimeSplit(2)}"
    println(s"RESULT DATE: $resultDate")
    val amarthaTransaction = List(
      AmarthaTransaction(None, "0", resultDate, "10.000", "123", "123", "Imbal Hasil")
    )
    when(amarthaService.processResult(any(), any())).thenReturn(amarthaResult)
    when(amarthaResult.transaction).thenReturn(amarthaTransaction)
    val amartha = new AmarthaScheduler(startTime, interval, amarthaService, http) with TelegramRequestBuilderMock
    println(amartha.task)
    true shouldBe true
  }

}
