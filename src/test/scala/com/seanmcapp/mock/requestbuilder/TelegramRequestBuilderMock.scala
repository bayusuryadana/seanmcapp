package com.seanmcapp.mock.requestbuilder

import com.seanmcapp.config.TelegramConf
import com.seanmcapp.repository.instagram.Photo
import com.seanmcapp.util.parser.TelegramResponse
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder
import spray.json._

import scala.io.Source

trait TelegramRequestBuilderMock extends TelegramRequestBuilder {

  override val telegramConf = TelegramConf("endpoint", "@seanmcbot")

  import com.seanmcapp.util.parser.TelegramJson._

  override def sendPhoto(chatId: Long, photo: Photo): TelegramResponse = {
    val outputFromFile = Source.fromResource("telegram/" + chatId + "_response.json")
    val output = outputFromFile.mkString.replace("{caption}", photo.account)
    output.parseJson.convertTo[TelegramResponse]
  }

  override def sendMessage(chatId: Long, text: String): TelegramResponse = {
    "".parseJson.convertTo[TelegramResponse]
  }

}
