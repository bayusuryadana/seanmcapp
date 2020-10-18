package com.seanmcapp.external

import com.seanmcapp.TelegramConf
import scalaj.http.MultiPart

// $COVERAGE-OFF$
class TelegramClient(http: HttpRequestClient) {

  val telegramConf: TelegramConf = TelegramConf()

  def sendPhoto(chatId: Long, photoUrl: String, caption: String): TelegramResponse = {
    val urlString = telegramConf.endpoint + "/sendphoto" +
      "?chat_id=" + chatId +
      "&photo=" + photoUrl +
      "&caption=" + caption

    val response = http.sendGetRequest(urlString)
    val result = decode[TelegramResponse](response)
    println(s"[INFO] send photo to chatId: $chatId")
    result
  }

  def sendMessage(chatId: Long, text: String): TelegramResponse = {
    val urlString = telegramConf.endpoint + "/sendmessage?chat_id=" + chatId + "&text=" + text + "&parse_mode=markdown"
    val response = http.sendGetRequest(urlString)
    val result = decode[TelegramResponse](response)
    println(s"[INFO] send message to chatId: $chatId with text: $text")
    result
  }

  def sendPhotoWithFileUpload(chatId: Long, caption: String = "", data: Array[Byte]): TelegramResponse = {
    val parts = MultiPart("photo", caption, "application/octet-stream", data)
    val params = Some(ParamMap(Map("chat_id" -> String.valueOf(chatId), "caption" -> caption)))

    val response = http.sendRequest(telegramConf.endpoint + "/sendphoto", params, multiPart = Some(parts))
    decode[TelegramResponse](response)
  }

}

// Telegram common
case class TelegramUser(id: Long, is_bot: Boolean, first_name: String, last_name: Option[String], username: Option[String])
case class TelegramChat(id: Long, `type`: String, title: Option[String], first_name: Option[String])

// Telegram Input (webhook)
case class TelegramUpdate(message: Option[TelegramMessage], callback_query: Option[TelegramCallbackQuery])
case class TelegramCallbackQuery(id: String, from: TelegramUser, data: String)
case class TelegramMessage(from: TelegramUser, chat: TelegramChat, text: Option[String], entities: Option[Seq[TelegramMessageEntity]])
case class TelegramMessageEntity(`type`: String, offset: Int, length: Int)

// Telegram Response (send message / photo response)
case class TelegramResult(message_id: Long, chat: TelegramChat, from: Option[TelegramUser], date: Int, text: Option[String], caption: Option[String])
case class TelegramResponse(ok: Boolean, result: TelegramResult)

