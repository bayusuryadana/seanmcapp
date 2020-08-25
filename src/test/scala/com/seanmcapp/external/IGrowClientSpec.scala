package com.seanmcapp.external

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source

class IGrowClientSpec extends AnyWordSpec with Matchers {

  "getList" in {
    val http = Mockito.mock(classOf[HttpRequestClient])
    val igrowClient = new IGrowClient(http)
    val mockResponse = Source.fromResource("scheduler/igrow_response.json").mkString
    when(http.sendGetRequest(any())).thenReturn(mockResponse)
    val expected = IgrowResponse(List(
      IgrowData("Gemilang Sarea Farm Eggs Project", 4000000, 26, "18% per annum", 3),
      IgrowData("Tilapia Sumber Nila Berkah", 4800000, 0, "18% per annum", 2),
      IgrowData("Goldfish Mayang Mas Sejahtera", 5000000, 0, "18% per annum", 2),
      IgrowData("Chicken Eggs Olat Maras Farm", 4991000, 0, "13% per annum", 3),
      IgrowData("eFishery Tech Village : Catfish", 5000000, 0, "14% per annum", 2),
      IgrowData("Corn in Dompu", 6200000, 0, "16 % per annum", 0.5)
    ))
    igrowClient.getList shouldBe expected
  }

}
