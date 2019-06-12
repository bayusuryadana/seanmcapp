package com.seanmcapp.util.parser

import com.seanmcapp.repository.instagram.Photo
import spray.json._

case class TelegramUpdate(message: Option[TelegramMessage], callbackQuery: Option[TelegramCallbackQuery])
case class TelegramCallbackQuery(id: String, from: TelegramUser, data: String)
case class TelegramMessage(from: TelegramUser, chat: TelegramChat, text: Option[String], entities: Option[Seq[TelegramMessageEntity]])
case class TelegramUser(id: Long, isBot: Boolean, firstName: String, lastName: Option[String], username: Option[String])
case class TelegramChat(id: Long, chatType: String, title: Option[String], firstName: Option[String])
case class TelegramMessageEntity(entityType: String, offset: Int, length: Int)

case class TelegramResponse(ok: Boolean, result: TelegramResult)
case class TelegramResult(messageId: Long, chat: TelegramChat, from: Option[TelegramUser], date: Int, text: Option[String], photo: Option[Seq[JsValue]], caption: Option[String])

object CBCJson extends DefaultJsonProtocol {

  implicit val photoFormat = jsonFormat(Photo, "id", "thumbnail_src", "date", "caption", "account")

  implicit val telegramUserFormat = jsonFormat(TelegramUser, "id", "is_bot", "first_name", "last_name", "username")
  implicit val telegramChatFormat = jsonFormat(TelegramChat, "id", "type", "title", "first_name")
  implicit val telegramMessageEntityFormat = jsonFormat(TelegramMessageEntity, "type", "offset", "length")
  implicit val telegramCallbackQueryFormat = jsonFormat(TelegramCallbackQuery, "id", "from", "data")
  implicit val telegramMessageFormat = jsonFormat(TelegramMessage, "from", "chat", "text", "entities")
  implicit val telegramUpdateFormat = jsonFormat(TelegramUpdate, "message", "callback_query")

  implicit val telegramResultFormat = jsonFormat(TelegramResult, "message_id", "chat", "from", "date", "text", "photo", "caption")
  implicit val telegramResponseFormat = jsonFormat(TelegramResponse, "ok", "result")

}
