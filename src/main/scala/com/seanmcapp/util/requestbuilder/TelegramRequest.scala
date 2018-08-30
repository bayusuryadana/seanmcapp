package com.seanmcapp.util.requestbuilder

import com.seanmcapp.config.TelegramConf
import com.seanmcapp.repository.Photo

import scalaj.http.{Http, HttpResponse}

trait TelegramRequest {

  val telegramConf = TelegramConf()
  val baseUrl = telegramConf.endpoint

  def getTelegramSendPhoto(chatId: Long, photo: Photo): HttpResponse[String] = {
    val photoId = photo.id
    val inlineKeyboard =
      s"""
        |{
        |"inline_keyboard":[
        |[
        |{"text":"dislike","callback_data":"-1:$photoId"},
        |{"text":"like","callback_data":"0:$photoId"},
        |{"text":"super like","callback_data":"1:$photoId"}
        |]
        |]
        |}
      """.stripMargin.replaceAll("\n", "")

    val urlString = baseUrl + "/sendphoto" +
      "?chat_id=" + chatId +
      "&photo=" + photo.thumbnailSrc +
      "&caption=" + photo.caption +
      "%0A%40" + photo.account +
      "&reply_markup=" + inlineKeyboard
    Http(urlString).asString
  }

  def getTelegramSendMessege(chatId: Long, text: String): HttpResponse[String] = {
    val urlString = baseUrl + "/sendmessage?chat_id=" + chatId + "&text=" + text
    Http(urlString).asString
  }

  def getAnswerCallbackQuery(queryId: String, notificationText: String): HttpResponse[String] = {
    val urlString = baseUrl + "/answerCallbackQuery?callback_query_id=" + queryId + "&text=" + notificationText
    Http(urlString).asString
  }

}
