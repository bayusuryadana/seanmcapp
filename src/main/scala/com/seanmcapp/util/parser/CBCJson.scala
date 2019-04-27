package com.seanmcapp.util.parser

import com.seanmcapp.repository.instagram.{Customer, Photo, Vote}
import spray.json._

case class TelegramUpdate(message: Option[TelegramMessage], callbackQuery: Option[TelegramCallbackQuery])
case class TelegramCallbackQuery(id: String, from: TelegramUser, data: String)
case class TelegramMessage(from: TelegramUser, chat: TelegramChat, text: Option[String], entities: Option[Seq[TelegramMessageEntity]])
case class TelegramUser(id: Long, firstName: String, lastName: Option[String], username: Option[String])
case class TelegramChat(id: Long, chatType: String, title: Option[String])
case class TelegramMessageEntity(entityType: String, offset: Int, length: Int)

object CBCJson extends DefaultJsonProtocol {

  implicit val customerFormat = jsonFormat3(Customer)
  implicit val voteFormat = jsonFormat(Vote, "photos_id", "customers_id", "rating")
  implicit val photoFormat = jsonFormat(Photo, "id", "thumbnail_src", "date", "caption", "account")

  implicit val telegramUserFormat = jsonFormat(TelegramUser, "id", "first_name", "last_name", "username")
  implicit val telegramChatFormat = jsonFormat(TelegramChat, "id", "type", "title")
  implicit val telegramMessageEntityFormat = jsonFormat(TelegramMessageEntity, "type", "offset", "length")
  implicit val telegramCallbackQueryFormat = jsonFormat(TelegramCallbackQuery, "id", "from", "data")
  implicit val telegramMessageFormat = jsonFormat(TelegramMessage, "from", "chat", "text", "entities")
  implicit val telegramUpdateFormat = jsonFormat(TelegramUpdate, "message", "callback_query")

}
