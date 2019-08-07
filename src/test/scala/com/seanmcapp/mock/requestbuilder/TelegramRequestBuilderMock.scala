package com.seanmcapp.mock.requestbuilder

import com.seanmcapp.config.TelegramConf
import com.seanmcapp.repository.instagram.Photo
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder
import scalaj.http.HttpResponse

import scala.io.Source

trait TelegramRequestBuilderMock extends TelegramRequestBuilder {

  override val telegramConf = TelegramConf("endpoint", "@seanmcbot")

  override def sendPhoto(chatId: Long, photo: Photo): HttpResponse[String] = {
    val outputFromFile = Source.fromResource("telegram/" + chatId + "_response.json")
    val output = outputFromFile.mkString.replace("{caption}", photo.account)
    HttpResponse[String](output, 200, Map.empty)
  }

  override def sendMessage(chatId: Long, text: String): HttpResponse[String] = {
    HttpResponse[String]("", 200, Map.empty)
  }

}
