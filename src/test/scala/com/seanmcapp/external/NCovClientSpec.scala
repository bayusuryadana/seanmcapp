package com.seanmcapp.external

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class NCovClientSpec extends AnyWordSpec with Matchers {

  "getReport" in {
    val http = Mockito.mock(classOf[HttpRequestClient])
    val ncovClient = new NCovClient(http)
    when(http.sendGetRequest(any())).thenReturn("Singapore/Indonesia,50")
    val expected = List("Singapore/Indonesia,50", "Singapore/Indonesia,50", "Singapore/Indonesia,50")
    ncovClient.getReport shouldBe expected
  }

}
