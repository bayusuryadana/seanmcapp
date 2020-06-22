package com.seanmcapp.service

import com.seanmcapp.config.StorageConf
import com.seanmcapp.mock.repository.{CustomerRepoMock, PhotoRepoMock}
import com.seanmcapp.mock.requestbuilder.{HttpRequestBuilderMock, TelegramRequestBuilderMock}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers
import scalacache.Cache
import scalacache.modes.sync._

import scala.io.Source
import spray.json._

class CBCServiceSpec extends AsyncWordSpec with Matchers {

  val storageConfMock = StorageConf("access", "secret", "host", "bucket")
  val knnUrl = s"${storageConfMock.host}/${storageConfMock.bucket}/knn.csv"
  val responseMap: Map[String, String] = Map(
    knnUrl -> Source.fromResource("instagram/knn.csv").mkString
  )
  val cbcService = new CBCService(PhotoRepoMock, CustomerRepoMock, new HttpRequestBuilderMock(responseMap))
    with TelegramRequestBuilderMock {
    override val storageConf = storageConfMock
  }

  "should return any random photos - API random endpoint" in {
    cbcService.random.map { res =>
      res.map(_.thumbnailSrc) shouldBe Some("https://someurl")
    }
  }

  "should return any random photos using private chat type input - telegram random endpoint" in {
    val input = Source.fromResource("telegram/274852283_request.json").mkString.parseJson
    cbcService.randomFlow(input).map { response =>

      response shouldNot be(None)
      val res = response.getOrElse(cancel("response is not defined"))

      res.ok shouldEqual true
      val chat = res.result.chat
      chat.id shouldEqual 274852283
      chat.chatType shouldEqual "private"
      chat.firstName shouldBe Some("Bayu")

      res.result.from.isDefined shouldEqual true
      val from = res.result.from.getOrElse(cancel("response is not defined"))
      from.id shouldEqual 354236808
      from.isBot shouldEqual true
      from.username shouldEqual Some("seanmcbot")

      res.result.photo.map(_.nonEmpty) shouldBe Some(true)
    }
  }

  "should return any random photos using group chat type input - telegram random endpoint" in {
    val input = Source.fromResource("telegram/-111546505_request.json").mkString.parseJson
    cbcService.randomFlow(input).map { response =>

      response shouldNot be(None)
      val res = response.getOrElse(cancel("response is not defined"))

      res.ok shouldEqual true
      val chat = res.result.chat
      chat.id shouldEqual -111546505
      chat.chatType shouldEqual "group"
      chat.title shouldBe Some("Kelompok abang redho")

      val from = res.result.from
      from.map(_.id) shouldBe Some(354236808)
      from.map(_.isBot) shouldBe Some(true)
      from.flatMap(_.username) shouldBe Some("seanmcbot")
    }
  }

  "command should return any random photo on particular account" in {
    val chatId = 274852283
    val command = "/cbc_ui_cantik"
    cbcService.executeCommand(command, chatId, 123L, "Fawwaz Afifanto").map { response =>

      response shouldNot be(None)
      val res = response.getOrElse(cancel("response is not defined"))

      res.ok shouldEqual true
      res.result.caption shouldEqual Some("Ritha Amelia D. Psikologi'12%0A%40ui.cantik")
    }
  }

  "command should return any random photo on particular account (2)" in {
    val chatId = 274852283
    val command = "/cbc_bidadari_ub"
    cbcService.executeCommand(command, chatId, 123L, "Fawwaz Afifanto").map { response =>

      response shouldNot be(None)
      val res = response.getOrElse(cancel("response is not defined"))

      res.ok shouldEqual true
      res.result.caption shouldEqual Some("Nadia Raissa. FISIP'13%0A%40bidadari.ub")
    }
  }

  "should get correct mapping for recommendation" in {
    val result = cbcService.getRecommendation
    result.keys shouldEqual Set(
      772020198343721705L, 1699704484487729075L, 2197263767212894174L, 2241324772649595331L, 1413884082743596438L, 1116926637369974369L)
  }

  "should return a recommendation photos based on recommendation csv file - telegram recommendation endpoint" in {
    // this test will be based on the last fetched photo in previous test above, please keep in mind
    val command = "/recommendation"
    val chatId = 274852283
    val userId = 123
    val userFullName = "Yukihira Soma"
    cbcService.executeCommand(command, chatId, userId, userFullName).map { response =>
      response shouldNot be(None)
      val res = response.getOrElse(cancel("response is not defined"))

      res.ok shouldEqual true
      val chat = res.result.chat
      chat.id shouldEqual 274852283
      res.result.caption shouldEqual Some("Delicia Gemma. Hukum 2011%0A%40unpad.geulis")
    }
  }
}
