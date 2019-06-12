package com.seanmcapp.mock.requestbuilder

import com.seanmcapp.repository.instagram.Photo
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder
import scalaj.http.HttpResponse

trait TelegramRequestBuilderMock extends TelegramRequestBuilder {

  override def sendPhoto(chatId: Long, photo: Photo): HttpResponse[String] = {
    HttpResponse[String]("", 200, Map.empty)
  }

  override def sendMessage(chatId: Long, text: String): HttpResponse[String] = {
    HttpResponse[String]("", 200, Map.empty)
  }

}
