package com.seanmcapp.service

import com.seanmcapp.CBCServiceImpl
import com.seanmcapp.util.parser.TelegramUpdate
import org.scalatest.{AsyncWordSpec, Matchers}

import scala.io.Source
import spray.json._

class CBCServiceSpec extends AsyncWordSpec with Matchers with CBCServiceImpl {

  import com.seanmcapp.util.parser.TelegramJson._

  "should return any random photos - API random endpoint" in {
    random.map { res =>
      res.isDefined shouldEqual true
      res.get.thumbnailSrc shouldEqual "https://someurl"
    }
  }

  "should return any random photos using private chat type input - telegram random endpoint" in {
    val input = Source.fromResource("telegram/274852283_request.json").mkString.parseJson.convertTo[TelegramUpdate].message.get
    randomFlow(input).map { response =>

      response shouldNot be(None)
      val res = response.get

      res.ok shouldEqual true
      val chat = res.result.chat
      chat.id shouldEqual 274852283
      chat.chatType shouldEqual "private"
      chat.firstName.isDefined shouldEqual true
      chat.firstName.get shouldEqual "Bayu"

      res.result.from.isDefined shouldEqual true
      val from = res.result.from.get
      from.id shouldEqual 354236808
      from.isBot shouldEqual true
      from.username.isDefined shouldEqual true
      from.username.get shouldEqual "seanmcbot"

      res.result.photo.isDefined shouldEqual true
      res.result.photo.get.size should be > 0
    }
  }

  "should return any random photos using group chat type input - telegram random endpoint" in {
    val input = Source.fromResource("telegram/-111546505_request.json").mkString.parseJson.convertTo[TelegramUpdate].message.get
    randomFlow(input).map { response =>

      response shouldNot be(None)
      val res = response.get

      res.ok shouldEqual true
      val chat = res.result.chat
      chat.id shouldEqual -111546505
      chat.chatType shouldEqual "group"
      chat.title.isDefined shouldEqual true
      chat.title.get shouldEqual "Kelompok abang redho"

      res.result.from.isDefined shouldEqual true
      val from = res.result.from.get
      from.id shouldEqual 354236808
      from.isBot shouldEqual true
      from.username.isDefined shouldEqual true
      from.username.get shouldEqual "seanmcbot"
    }
  }

  "command should return any random photo on particular account" in {
    val chatId = 274852283
    val command = "/cbc_ui_cantik"
    executeCommand(command, chatId, 123L, "Fawwaz Afifanto").map { response =>

      response shouldNot be(None)
      val res = response.get

      res.ok shouldEqual true
      res.result.caption.isDefined shouldEqual true
      res.result.caption.get shouldEqual "ui.cantik"
    }
  }

  "command should return any random photo on particular account (2)" in {
    val chatId = 274852283
    val command = "/cbc_bidadari_ub"
    executeCommand(command, chatId, 123L, "Fawwaz Afifanto").map { response =>

      response shouldNot be(None)
      val res = response.get

      res.ok shouldEqual true
      res.result.caption.isDefined shouldEqual true
      res.result.caption.get shouldEqual "bidadari.ub"
    }
  }
}
