package com.seanmcapp.external

import com.seanmcapp.util.{DsdaWaterGate, DsdaWaterGateResponse}
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DsdaJakartaClientSpec extends AnyWordSpec with Matchers {

  "getReport" in {
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
    val http = Mockito.mock(classOf[HttpRequestClient])
    when(http.sendGetRequest(anyString())).thenReturn(mockXml)
    val dsdaJakartaScheduler = new DsdaJakartaClient(http)
    val expected = DsdaWaterGateResponse(List(
      DsdaWaterGate("Manggarai", "Status : Siaga 3"),
      DsdaWaterGate("Bekasi", "Status : Normal"),
      DsdaWaterGate("Depok", "Status : Siaga 2")
    ))
    dsdaJakartaScheduler.getReport shouldBe expected
  }

}
