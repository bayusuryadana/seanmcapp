package com.seanmcapp.util.parser

import spray.json._

case class TelegramUpdate(message: Option[TelegramMessage], callbackQuery: Option[TelegramCallbackQuery])
case class TelegramCallbackQuery(id: String, from: TelegramUser, data: String)
case class TelegramMessage(from: TelegramUser, chat: TelegramChat, text: String, entities: Option[Seq[TelegramMessageEntity]])
case class TelegramUser(id: Long, firstName: String, lastName: Option[String], username: Option[String])
case class TelegramChat(id: Long, chatType: String, title: Option[String])
case class TelegramMessageEntity(entityType: String, offset: Int, length: Int)

object TelegramJson extends DefaultJsonProtocol {

  implicit val telegramUserFormat = jsonFormat(TelegramUser, "id", "first_name", "last_name", "username")
  implicit val telegramChatFormat = jsonFormat(TelegramChat, "id", "type", "title")
  implicit val telegramMessageEntityFormat = jsonFormat(TelegramMessageEntity, "type", "offset", "length")
  implicit val telegramCallbackQueryFormat = jsonFormat(TelegramCallbackQuery, "id", "from", "data")
  implicit val telegramMessageFormat = jsonFormat(TelegramMessage, "from", "chat", "text", "entities")
  implicit val telegramUpdateFormat = jsonFormat(TelegramUpdate, "message", "callback_query")

}
