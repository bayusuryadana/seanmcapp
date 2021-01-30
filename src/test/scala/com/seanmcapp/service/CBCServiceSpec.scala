package com.seanmcapp.service

import com.seanmcapp.repository.{CustomerRepoMock, PhotoRepoMock}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.io.Source

class CBCServiceSpec extends AsyncWordSpec with Matchers {

  import com.seanmcapp.external._

  val cbcClient = Mockito.mock(classOf[CBCClient])
  val responseMock = Source.fromResource("instagram/knn.csv").getLines().map { line =>
    val items = line.split(",")
    items.head.toLong -> items.tail.map(_.toLong)
  }.toMap
  when(cbcClient.getRecommendation).thenReturn(responseMock)
  val cbcService = new CBCService(PhotoRepoMock, CustomerRepoMock, cbcClient)

  val userId = 274852283
  val userFullName = "Yukihira Soma"

  "should return any random photos - API random endpoint" in {
    cbcService.random.map { res =>
      res.map(_.thumbnailSrc) shouldBe Some("https://someurl")
    }
  }

  "should return a random photos - cbc" in {
    cbcService.cbcFlow(userId, userFullName, "cbc").map { response =>
      response shouldNot be(None)
    }
  }

  "should return a recommendation photos based on recommendation csv file - recommendation" in {
    // this test will be based on the last fetched photo in previous test above, please keep in mind
    cbcService.cbcFlow(userId, userFullName, "recommendation").map { response =>
      response shouldNot be(None)
      val res = response.getOrElse(cancel("response is not defined"))
      res.id shouldEqual 884893623514815734L
      res.caption shouldEqual "Delicia Gemma. Hukum 2011"
      res.account shouldEqual "unpad.geulis"
    }
  }

  "should throw an exception if command is not valid" in {
    assertThrows[Exception] {
      cbcService.cbcFlow(1, "name", "wow")
    }
  }
}
