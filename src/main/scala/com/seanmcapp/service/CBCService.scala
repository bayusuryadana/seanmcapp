package com.seanmcapp.service

import com.seanmcapp.repository.instagram._
import com.seanmcapp.util.parser.{TelegramMessage, TelegramResponse}
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder
import spray.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait CBCService extends TelegramRequestBuilder {

  val photoRepo: PhotoRepo

  import com.seanmcapp.util.parser.CBCJson._

  def random: Future[Option[Photo]] = photoRepo.getRandom(None)

  def randomFlow(message: TelegramMessage): Future[TelegramResponse] = {
    val chatId = message.chat.id
    message.entities.getOrElse(Seq.empty).headOption match {
      case Some(entity) =>
        val command = message.text.getOrElse("")
          .substring(entity.offset, entity.offset + entity.length)
          .stripSuffix(telegramConf.botname)
        executeCommand(command, chatId)

      case _ => throw new Exception("Telegram Message Entities not found")
    }
  }

  protected def executeCommand(command: String, chatId: Long): Future[TelegramResponse] = {
    command.split("_").head match {
      case "/cbc" =>
        val account = if (command.split("_").length > 1) Some(command.replace("_", ".").stripPrefix("/cbc.")) else None
        photoRepo.getRandom(account).map(_.map { photo =>
          sendPhoto(chatId, photo).body.parseJson.convertTo[TelegramResponse]
        }.getOrElse(throw new Exception("photo not found")))
      case _ => throw new Exception("command not found")
    }
  }

}
