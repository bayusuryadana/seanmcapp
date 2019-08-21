package com.seanmcapp.util.parser

import spray.json._

case class TelegramResponse(ok: Boolean, result: TelegramResult)
case class TelegramResult(messageId: Long, chat: TelegramChat, from: Option[TelegramUser], date: Int, text: Option[String], photo: Option[Seq[JsValue]], caption: Option[String])

trait TelegramOutputEncoder extends Encoder with TelegramCommon {
  implicit val telegramResultFormat = jsonFormat(TelegramResult, "message_id", "chat", "from", "date", "text", "photo", "caption")
  implicit val telegramResponseFormat = jsonFormat(TelegramResponse, "ok", "result")
}
