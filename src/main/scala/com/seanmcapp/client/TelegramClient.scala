package com.seanmcapp.client

import java.net.{URL, URLEncoder}

import com.seanmcapp.util.TelegramConf
import scalaj.http.MultiPart

// $COVERAGE-OFF$
class TelegramClient(http: HttpRequestClient) {

  val telegramConf: TelegramConf = TelegramConf()

  def sendPhoto(chatId: Long, photoUrl: String, caption: String): TelegramResponse = {
    val sanitizedCaption = URLEncoder.encode(caption, "UTF-8")
    val urlString = s"${telegramConf.endpoint}/sendphoto?chat_id=$chatId&photo=$photoUrl&caption=$sanitizedCaption"
    val response = http.sendGetRequest(urlString)
    val result = decode[TelegramResponse](response)
    println(s"[INFO] send photo to chatId: $chatId with caption: $sanitizedCaption")
    println(s"$photoUrl")
    result
  }

  def sendMessage(chatId: Long, text: String): TelegramResponse = {
    val sanitizedText = URLEncoder.encode(text, "UTF-8")
    val urlString = s"${telegramConf.endpoint}/sendmessage?chat_id=$chatId&text=$sanitizedText&parse_mode=markdown&disable_web_page_preview=true&disable_notification=true"
    val response = http.sendGetRequest(urlString)
    val result = decode[TelegramResponse](response)
    println(s"[INFO] send message to chatId: $chatId with text: $sanitizedText")
    result
  }

  def sendPhotoWithFileUpload(chatId: Long, caption: String = "", data: Array[Byte]): TelegramResponse = {
    val parts = MultiPart("photo", caption, "application/octet-stream", data)
    val params = Some(ParamMap(Map("chat_id" -> String.valueOf(chatId), "caption" -> caption)))

    val response = http.sendRequest(s"${telegramConf.endpoint}/sendphoto", params, multiPart = Some(parts))
    println(s"[INFO] send photo to chatId: $chatId")
    decode[TelegramResponse](response.body)
  }

  def sendVideoWithFileUpload(chatId: Long, caption: String = "", data: Array[Byte]): TelegramResponse = {
    val parts = MultiPart("video", caption, "application/octet-stream", data)
    val params = Some(ParamMap(Map("chat_id" -> String.valueOf(chatId), "caption" -> caption)))

    val response = http.sendRequest(s"${telegramConf.endpoint}/sendvideo", params, multiPart = Some(parts))
    println(s"[INFO] send video to chatId: $chatId")
    decode[TelegramResponse](response.body)
  }

  def getDataByteFromUrl(url: String): Array[Byte] = {
    val inputStream = new URL(url).openStream
    LazyList.continually(inputStream.read).takeWhile(_ != -1).map(_.toByte).toArray
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

