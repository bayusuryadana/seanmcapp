package com.seanmcapp.service

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes.OK
import com.seanmcapp.external.{TelegramClient, TelegramResponse, TelegramUpdate}
import com.seanmcapp.util.ExceptionHandler

import scala.concurrent.Future

class TelegramWebhookService(telegramClient: TelegramClient) {

  def receive(telegramUpdate: TelegramUpdate): Future[Option[TelegramResponse]] = {
    val message = telegramUpdate.message.getOrElse(throw new Exception("This request is does not have a message"))
    val chatId = message.chat.id
    val userId = message.from.id
    val userFullName = message.from.first_name + " " + message.from.last_name.getOrElse("")

    message.entities.getOrElse(List.empty).headOption match {
      case Some(entity) =>
        val command = message.text.flatMap(_
          .substring(entity.offset, entity.offset + entity.length)
          .stripSuffix(telegramClient.telegramConf.botname)
          .split("_")
          .headOption
        )
        command match {
//          case Some(s) if s == "/cbc" =>
//            CBCService.cbcFlow(userId, userFullName, "cbc").map(_.map(p => cbcSendPhoto(p, chatId)))
//          case Some(s) if s == "/recommendation" =>
//            CBCService.cbcFlow(userId, userFullName, "recommendation").map(_.map(p => cbcSendPhoto(p, chatId)))
//          case Some(s) if s == "/hadith" =>
//            val hadith = hadithService.random
//            val telegramRes = telegramClient.sendMessage(chatId, hadith)
//            Future.successful(Some(telegramRes))
          case _ =>
            throw new TelegramWebhookException(s"Command not recognized: $command")
        }
      case _ =>
        throw new TelegramWebhookException("No entities (command) found")
    }
  }

}

class TelegramWebhookException(message: String) extends ExceptionHandler(new Exception(message)) {
  override val responseCode: StatusCode = OK
}
