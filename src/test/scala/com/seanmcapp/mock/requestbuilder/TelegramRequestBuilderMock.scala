package com.seanmcapp.mock.requestbuilder

import com.seanmcapp.config.TelegramConf
import com.seanmcapp.repository.instagram.Photo
import com.seanmcapp.util.parser.encoder.TelegramResponse
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder

import scala.io.Source

trait TelegramRequestBuilderMock extends TelegramRequestBuilder {

  override val telegramConf = TelegramConf("endpoint", "@seanmcbot")

  override def sendPhoto(chatId: Long, photo: Photo): TelegramResponse = {
    val source = Source.fromResource("telegram/" + chatId + "_response.json").mkString.replace("{caption}", photo.account)
    decode[TelegramResponse](source)
  }

  override def sendMessage(chatId: Long, text: String): TelegramResponse = {
    decode[TelegramResponse]("")
  }

}
