package com.seanmcapp

import com.seanmcapp.repository.Photo
import com.seanmcapp.util.requestbuilder.TelegramRequest

import scalaj.http.HttpResponse

trait TelegramRequestMock extends TelegramRequest {

  override def getTelegramSendPhoto(chatId: Long, photo: Photo, prefix: String =""): HttpResponse[String] = {
    HttpResponse[String]("", 200, Map.empty)
  }

  override def getTelegramSendMessege(chatId: Long, text: String): HttpResponse[String] = {
    HttpResponse[String]("", 200, Map.empty)
  }

  override def getAnswerCallbackQuery(queryId: String, notificationText: String): HttpResponse[String] = {
    HttpResponse[String]("", 200, Map.empty)
  }

}
