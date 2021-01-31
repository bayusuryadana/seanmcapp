package com.seanmcapp.service

import com.seanmcapp.TelegramConf
import com.seanmcapp.external.{TelegramClient, TelegramResponse, TelegramUpdate}
import com.seanmcapp.repository.instagram.Photo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TelegramWebhookService(CBCService: CBCService, hadithService: HadithService, telegramClient: TelegramClient) {

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
          case Some(s) if s == "/cbc" =>
            CBCService.cbcFlow(userId, userFullName, "cbc").map(_.map(p => cbcSendPhoto(p, chatId)))
          case Some(s) if s == "/recommendation" =>
            CBCService.cbcFlow(userId, userFullName, "recommendation").map(_.map(p => cbcSendPhoto(p, chatId)))
          case Some(s) if s == "/hadith" =>
            val hadith = hadithService.random
            val telegramRes = telegramClient.sendMessage(chatId, hadith)
            Future.successful(Some(telegramRes))
          case _ =>
            println("[ERROR] Command not recognized: " + command)
            Future.successful(None)
        }
      case _ =>
        println("[ERROR] No entities (command) found")
        Future.successful(None)
    }
  }

  private def cbcSendPhoto(photo: Photo, chatId: Long): TelegramResponse = {
    val caption = s"${photo.caption}\n@${photo.account}"
    telegramClient.sendPhoto(chatId, CBCService.getPhotoUrl(photo.id), caption)
  }

}
