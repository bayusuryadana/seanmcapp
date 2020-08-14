package com.seanmcapp.external

import com.seanmcapp.config.TelegramConf
import spray.json.JsValue

import scala.concurrent.Future

class TelegramClient(http: HttpRequestClient) {

  val telegramConf = TelegramConf()

  def sendPhoto(chatId: Long, photoUrl: String, caption: String): Future[Either[String, TelegramResponse]] = {
    val urlString = telegramConf.endpoint + "/sendphoto" +
      "?chat_id=" + chatId +
      "&photo=" + photoUrl +
      "&caption=" + caption

    val response = http.sendRequest(urlString)
    decode[TelegramResponse](response)
  }

  def sendMessage(chatId: Long, text: String): Future[Either[String, TelegramResponse]] = {
    val urlString = telegramConf.endpoint + "/sendmessage?chat_id=" + chatId + "&text=" + text + "&parse_mode=markdown"
    val response = http.sendRequest(urlString)
    decode[TelegramResponse](response)
  }

  def sendPhotoWithFileUpload(chatId: Long, caption: String = "", data: Array[Byte]): Future[Either[String, TelegramResponse]] = ???

//  def sendPhotoWithFileUpload(chatId: Long, caption: String = "", data: Array[Byte]): Future[Either[String, TelegramResponse]] = {
//    val parts = MultiPart("photo", caption, "application/octet-stream", data)
//    val params = Some(ParamMap(Map("chat_id" -> String.valueOf(chatId), "caption" -> caption)))
//
//    val response = http.sendRequest(telegramConf.endpoint + "/sendphoto", params, multiPart = Some(parts))
//    decode[TelegramResponse](response)
//  }

}

// Telegram common
case class TelegramUser(id: Long, isBot: Boolean, firstName: String, lastName: Option[String], username: Option[String])
case class TelegramChat(id: Long, chatType: String, title: Option[String], firstName: Option[String])

// Telegram Input (webhook)
case class TelegramUpdate(message: Option[TelegramMessage], callbackQuery: Option[TelegramCallbackQuery])
case class TelegramCallbackQuery(id: String, from: TelegramUser, data: String)
case class TelegramMessage(from: TelegramUser, chat: TelegramChat, text: Option[String], entities: Option[Seq[TelegramMessageEntity]])
case class TelegramMessageEntity(entityType: String, offset: Int, length: Int)

// Telegram Response (send message / photo response)
case class TelegramResponse(ok: Boolean, result: TelegramResult)
case class TelegramResult(messageId: Long, chat: TelegramChat, from: Option[TelegramUser], date: Int, text: Option[String], photo: Option[Seq[JsValue]], caption: Option[String])

