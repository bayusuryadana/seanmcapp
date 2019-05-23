package com.seanmcapp.service

import com.seanmcapp.repository.instagram._
import com.seanmcapp.util.parser.TelegramMessage
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait CBCService extends TelegramRequestBuilder {

  val photoRepo: PhotoRepo

  def random: Future[Option[Photo]] = photoRepo.getRandom(None)

  def randomFlow(message: TelegramMessage): Future[Int] = {
    message.entities.getOrElse(Seq.empty).headOption match {
      case Some(entity) =>
        val command = message.text.getOrElse("")
          .substring(entity.offset, entity.offset + entity.length)
          .stripSuffix(telegramConf.botname)

        command.split("_").head match {
          case "/cbc" =>
            val account = if (command.split("_").length > 1) Some(command.replace("_", ".").stripPrefix("/cbc.")) else None
            photoRepo.getRandom(account).map(_.map { photo =>
              sendPhoto(message.chat.id, photo).code
            }.getOrElse(throw new Exception("photo not found")))
          case _ => throw new Exception("command not found")
        }
      case _ => throw new Exception("Telegram Message Entities not found")
    }
  }

}
