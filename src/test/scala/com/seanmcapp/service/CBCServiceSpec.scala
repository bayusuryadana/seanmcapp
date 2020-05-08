package com.seanmcapp.service

import com.seanmcapp.mock.repository.{CustomerRepoMock, PhotoRepoMock}
import com.seanmcapp.mock.requestbuilder.TelegramRequestBuilderMock
import com.seanmcapp.util.requestbuilder.HttpRequestBuilderImpl
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source
import spray.json._

class CBCServiceSpec extends AsyncWordSpec with Matchers {

  val cbcService = new CBCService(PhotoRepoMock, CustomerRepoMock, HttpRequestBuilderImpl) with TelegramRequestBuilderMock

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
      res.result.caption shouldEqual Some("ui.cantik")
    }
  }

  "command should return any random photo on particular account (2)" in {
    val chatId = 274852283
    val command = "/cbc_bidadari_ub"
    cbcService.executeCommand(command, chatId, 123L, "Fawwaz Afifanto").map { response =>

      response shouldNot be(None)
      val res = response.getOrElse(cancel("response is not defined"))

      res.ok shouldEqual true
      res.result.caption shouldEqual Some("bidadari.ub")
    }
  }
}
