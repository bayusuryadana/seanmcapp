package com.seanmcapp.service

import com.seanmcapp.{StorageConf, TelegramConf}
import com.seanmcapp.repository.{CustomerRepoMock, PhotoRepoMock}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.io.Source

class CBCServiceSpec extends AsyncWordSpec with Matchers {

  import com.seanmcapp.external._

  val cbcClient = Mockito.mock(classOf[CBCClient])
  val telegramClient = new CBCTelegramClientMock
  val telegramConf = TelegramConf("endpoint", "@seanmcbot")
  val storageConf = StorageConf("access", "secret", "host", "bucket")
  when(cbcClient.storageConf).thenReturn(storageConf)
  val responseMock = Source.fromResource("instagram/knn.csv").getLines().map { line =>
    val items = line.split(",")
    items.head.toLong -> items.tail.map(_.toLong)
  }.toMap
  when(cbcClient.getRecommendation).thenReturn(responseMock)
  val cbcService = new CBCService(PhotoRepoMock, CustomerRepoMock, cbcClient, telegramClient)

  "should return any random photos - API random endpoint" in {
    cbcService.random.map { res =>
      res.map(_.thumbnailSrc) shouldBe Some("https://someurl")
    }
  }

  "should return any random photos using private chat type input - telegram random endpoint" in {
    val input = Source.fromResource("telegram/274852283_request.json").mkString
    val telegramUpdate = decode[TelegramUpdate](input)
    cbcService.randomFlow(telegramUpdate).map { response =>

      response shouldNot be(None)
      val res = response.getOrElse(cancel("response is not defined"))

      res.ok shouldEqual true
      val chat = res.result.chat
      chat.id shouldEqual 274852283
      chat.`type` shouldEqual "private"
      chat.first_name shouldBe Some("Bayu")

      res.result.from.isDefined shouldEqual true
      val from = res.result.from.getOrElse(cancel("response is not defined"))
      from.id shouldEqual 354236808
      from.is_bot shouldEqual true
      from.username shouldEqual Some("seanmcbot")
    }
  }

  "should return any random photos using group chat type input - telegram random endpoint" in {
    val input = Source.fromResource("telegram/-111546505_request.json").mkString
    val telegramUpdate = decode[TelegramUpdate](input)
    cbcService.randomFlow(telegramUpdate).map { response =>

      response shouldNot be(None)
      val res = response.getOrElse(cancel("response is not defined"))

      res.ok shouldEqual true
      val chat = res.result.chat
      chat.id shouldEqual -111546505
      chat.`type` shouldEqual "group"
      chat.title shouldBe Some("Kelompok abang redho")

      val from = res.result.from
      from.map(_.id) shouldBe Some(354236808)
      from.map(_.is_bot) shouldBe Some(true)
      from.flatMap(_.username) shouldBe Some("seanmcbot")
    }
  }

  "should return none given non valid TelegramUpdate" in {
    val telegramUpdate = Mockito.mock(classOf[TelegramUpdate])
    val telegramMessage = TelegramMessage(TelegramUser(1, false, "Bayu", None, None), TelegramChat(1, "type", None, None), None, None)
    when(telegramUpdate.message).thenReturn(Some(telegramMessage))
    cbcService.randomFlow(telegramUpdate).map { response =>
      response shouldBe None
    }
  }

  "should return none given TelegramUpdate with unrecognised command" in {
    val telegramUpdate = Mockito.mock(classOf[TelegramUpdate])
    val telegramMessageEntity = TelegramMessageEntity("type", 1, 3)
    val telegramMessage = TelegramMessage(TelegramUser(1, false, "Bayu", None, None), TelegramChat(1, "type", None, None), Some("/wow"), Some(Seq(telegramMessageEntity)))
    when(telegramUpdate.message).thenReturn(Some(telegramMessage))
    cbcService.randomFlow(telegramUpdate).map { response =>
      response shouldBe None
    }
  }

  "should return a recommendation photos based on recommendation csv file - telegram recommendation endpoint" in {
    // this test will be based on the last fetched photo in previous test above, please keep in mind
    val userId = 274852283
    val userFullName = "Yukihira Soma"
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
