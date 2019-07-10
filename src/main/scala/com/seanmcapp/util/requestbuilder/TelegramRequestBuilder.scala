package com.seanmcapp.util.requestbuilder

import com.seanmcapp.config.{DriveConf, TelegramConf}
import com.seanmcapp.repository.instagram.Photo

import scalaj.http.{Http, HttpResponse}

trait TelegramRequestBuilder {

  val telegramConf = TelegramConf()

  def sendPhoto(chatId: Long, photo: Photo): HttpResponse[String] = {
    val photoId = photo.id

    val urlString = telegramConf.endpoint + "/sendphoto" +
      "?chat_id=" + chatId +
      "&photo=" + DriveConf().url + photoId + ".jpg" +
      "&caption=" + photo.caption +
      "%0A%40" + photo.account
    Http(urlString).asString
  }

  def sendMessage(chatId: Long, text: String): HttpResponse[String] = {
    val urlString = telegramConf.endpoint + "/sendmessage?chat_id=" + chatId + "&text=" + text
    Http(urlString).asString
  }

}
