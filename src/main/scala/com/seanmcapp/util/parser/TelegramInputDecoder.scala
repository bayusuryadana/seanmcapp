package com.seanmcapp.util.parser

case class TelegramUpdate(message: Option[TelegramMessage], callbackQuery: Option[TelegramCallbackQuery])
case class TelegramCallbackQuery(id: String, from: TelegramUser, data: String)
case class TelegramMessage(from: TelegramUser, chat: TelegramChat, text: Option[String], entities: Option[Seq[TelegramMessageEntity]])
case class TelegramMessageEntity(entityType: String, offset: Int, length: Int)

trait TelegramInputDecoder extends Decoder with TelegramCommon {
  implicit val telegramMessageEntityFormat = jsonFormat(TelegramMessageEntity, "type", "offset", "length")
  implicit val telegramCallbackQueryFormat = jsonFormat(TelegramCallbackQuery, "id", "from", "data")
  implicit val telegramMessageFormat = jsonFormat(TelegramMessage, "from", "chat", "text", "entities")
  implicit val telegramUpdateFormat = jsonFormat(TelegramUpdate, "message", "callback_query")
}
