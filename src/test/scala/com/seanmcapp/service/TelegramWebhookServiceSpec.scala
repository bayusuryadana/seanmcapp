package com.seanmcapp.service

import com.seanmcapp.client.{TelegramChat, TelegramMessage, TelegramMessageEntity, TelegramUpdate, TelegramUser}
import com.seanmcapp.client.decode
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.io.Source

class TelegramWebhookServiceSpec extends AsyncWordSpec with Matchers {

  val telegramClient = new TelegramWebhookTelegramClientMock
  val telegramWebookService = new TelegramWebhookService(telegramClient)

  "should return any random photos using private chat type input - cbc" in {
    val chatId = 274852283L
    val input = Source.fromResource(s"telegram/${chatId}_request.json").mkString
    val telegramUpdate = decode[TelegramUpdate](input)
    telegramWebookService.receive(telegramUpdate).map { response =>
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

  "should return any random photos using group chat type input - cbc" in {
    val chatId = -111546505L
    val input = Source.fromResource(s"telegram/${chatId}_request.json").mkString
    val telegramUpdate = decode[TelegramUpdate](input)
    telegramWebookService.receive(telegramUpdate).map { response =>
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

  "should return any random hadith using private chat type input - hadith" in {
    val chatId = 274852283L
    val input = Source.fromResource(s"telegram/${chatId}_request.json").mkString
    val telegramUpdate = decode[TelegramUpdate](input)
    val modifiedTelegramUpdate = telegramUpdate.copy(message = telegramUpdate.message.map(_.copy(
      text = Some("/hadith"),
      entities = Some(Seq(TelegramMessageEntity("bot_command", 0, 7)))
    )))
    telegramWebookService.receive(modifiedTelegramUpdate).map { response =>
      response shouldNot be(None)
      val res = response.getOrElse(cancel("response is not defined"))

      res.result.text shouldBe Some("some hadith")
    }
  }

  "should return none given non valid TelegramUpdate" in {
    val telegramUpdate = Mockito.mock(classOf[TelegramUpdate])
    val telegramMessage = TelegramMessage(TelegramUser(1, false, "Bayu", None, None), TelegramChat(1, "type", None, None), None, None)
    when(telegramUpdate.message).thenReturn(Some(telegramMessage))
    assertThrows[TelegramWebhookException] {
      telegramWebookService.receive(telegramUpdate)
    }
  }

  "should return none given TelegramUpdate with unrecognised command" in {
    val telegramUpdate = Mockito.mock(classOf[TelegramUpdate])
    val telegramMessageEntity = TelegramMessageEntity("type", 1, 3)
    val telegramMessage = TelegramMessage(TelegramUser(1, false, "Bayu", None, None), TelegramChat(1, "type", None, None), Some("/wow"), Some(Seq(telegramMessageEntity)))
    when(telegramUpdate.message).thenReturn(Some(telegramMessage))
    assertThrows[TelegramWebhookException] {
      telegramWebookService.receive(telegramUpdate) 
    }
  }

}
