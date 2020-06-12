package com.seanmcapp.mock.requestbuilder

import com.seanmcapp.config.TelegramConf
import com.seanmcapp.util.parser.encoder.TelegramResponse
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder

import scala.io.Source

trait TelegramRequestBuilderMock extends TelegramRequestBuilder {

  override val telegramConf = TelegramConf("endpoint", "@seanmcbot")

  override def sendPhoto(chatId: Long, photoUrl: String, caption: String): TelegramResponse = {
    val source = Source.fromResource("telegram/" + chatId + "_response.json").mkString.replace("{caption}", caption)
    decode[TelegramResponse](source)
  }

  override def sendMessage(chatId: Long, text: String): TelegramResponse = {
    decode[TelegramResponse](defaultSendMessageResponse)
  }

  private val defaultSendMessageResponse: String =
  """
    |{
    |  "ok": true,
    |  "result": {
    |    "message_id": 12345,
    |    "from": {
    |      "id": 987654321,
    |      "is_bot": true,
    |      "first_name": "seanmcbot",
    |      "username": "seanmcbot"
    |    },
    |    "chat": {
    |      "id": 123456789,
    |      "first_name": "Sean",
    |      "type": "private"
    |    },
    |    "date": 1566468039,
    |    "text": "hello"
    |  }
    |}
  """.stripMargin

}
