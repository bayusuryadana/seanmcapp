package com.seanmcapp.util.parser

import spray.json.DefaultJsonProtocol

case class TelegramUser(id: Long, isBot: Boolean, firstName: String, lastName: Option[String], username: Option[String])
case class TelegramChat(id: Long, chatType: String, title: Option[String], firstName: Option[String])

trait TelegramCommon extends DefaultJsonProtocol {
  implicit val telegramUserFormat = jsonFormat(TelegramUser, "id", "is_bot", "first_name", "last_name", "username")
  implicit val telegramChatFormat = jsonFormat(TelegramChat, "id", "type", "title", "first_name")
}
