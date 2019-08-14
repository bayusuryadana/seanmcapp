package com.seanmcapp.service

import com.seanmcapp.repository.instagram._
import com.seanmcapp.util.parser.{TelegramMessage, TelegramResponse}
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait CBCService extends TelegramRequestBuilder {

  val photoRepo: PhotoRepo
  val customerRepo: CustomerRepo

  def random: Future[Option[Photo]] = photoRepo.getRandom(None)

  def randomFlow(message: TelegramMessage): Future[Option[TelegramResponse]] = {
    val chatId = message.chat.id
    val userId = message.from.id
    val userFullName = message.from.firstName + " " + message.from.lastName.getOrElse("")
    message.entities.getOrElse(Seq.empty).headOption match {
      case Some(entity) =>
        val command = message.text.getOrElse("")
          .substring(entity.offset, entity.offset + entity.length)
          .stripSuffix(telegramConf.botname)
        executeCommand(command, chatId, userId, userFullName)

      case _ =>
        println("[ERROR] Telegram Message Entities not found")
        Future.successful(None)
    }
  }

  protected def executeCommand(command: String, chatId: Long, userId: Long, userFullName: String): Future[Option[TelegramResponse]] = {
    val customerF = customerRepo.get(userId)
    command.split("_").head match {
      case "/cbc" =>
        val account = if (command.split("_").length > 1) Some(command.replace("_", ".").stripPrefix("/cbc.")) else None
        val photoF = photoRepo.getRandom(account)
        for {
          customerOpt <- customerF
          photoOpt <- photoF
        } yield {
          photoOpt.map { photo =>
            customerOpt match {
              case Some(customer) => customerRepo.update(Customer(userId, userFullName, customer.count + 1))
              case None => customerRepo.insert(Customer(userId, userFullName, 1))
            }
            sendPhoto(chatId, photo)
          }
        }
      case _ =>
        println("[ERROR] Command not found")
        Future.successful(None)
    }
  }

}
