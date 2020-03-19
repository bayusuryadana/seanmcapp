package com.seanmcapp.scheduler

import com.seanmcapp.SchedulerForTest
import com.seanmcapp.mock.requestbuilder.TelegramRequestBuilderMock
import org.scalatest.{AsyncWordSpec, Matchers}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._

class DsdaJakartaSchedulerSpec extends AsyncWordSpec with Matchers with SchedulerForTest{

  "DsdaJakartaScheduler should return correctly" in {
    val dsdaJakartaScheduler = new DsdaJakartaScheduler(startTime, interval, http) with TelegramRequestBuilderMock
    val mockXml =
      """
        | <DocumentElement>
        |  <SP_GET_LAST_STATUS_PINTU_AIR>
        |    <NAMA_PINTU_AIR>Manggarai</NAMA_PINTU_AIR>
        |    <STATUS_SIAGA>Status : Siaga 3</STATUS_SIAGA>
        |  </SP_GET_LAST_STATUS_PINTU_AIR>
        |  <SP_GET_LAST_STATUS_PINTU_AIR>
        |    <NAMA_PINTU_AIR>Bekasi</NAMA_PINTU_AIR>
        |    <STATUS_SIAGA>Status : Normal</STATUS_SIAGA>
        |  </SP_GET_LAST_STATUS_PINTU_AIR>
        |  <SP_GET_LAST_STATUS_PINTU_AIR>
        |    <NAMA_PINTU_AIR>Depok</NAMA_PINTU_AIR>
        |    <STATUS_SIAGA>Status : Siaga 2</STATUS_SIAGA>
        |  </SP_GET_LAST_STATUS_PINTU_AIR>
        |</DocumentElement>
        |""".stripMargin

    when(http.sendGetRequest(anyString())).thenReturn(mockXml)
    dsdaJakartaScheduler.task shouldBe "Seanmcapp melaporkan pintu air siaga:\n\nManggarai: Siaga 3\nDepok: Siaga 2"
  }

}
