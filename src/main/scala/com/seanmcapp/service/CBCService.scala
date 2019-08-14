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
    val result = message.entities.getOrElse(Seq.empty).headOption match {
      case Some(entity) =>
        val command = message.text.getOrElse("")
          .substring(entity.offset, entity.offset + entity.length)
          .stripSuffix(telegramConf.botname)
        executeCommand(command, chatId, userId, userFullName)

      case _ =>
        println("[ERROR] No entities (command) found")
        Future.successful(None)
    }

    // logging group chat :D
    if (message.chat.chatType != "private")
      println(s"[CHAT] $userFullName@${message.chat.title.getOrElse("???")}:${message.text.getOrElse("???")}")

    result
  }

  protected def executeCommand(command: String, chatId: Long, userId: Long, userFullName: String): Future[Option[TelegramResponse]] = {
    command.split("_").head match {
      case "/cbc" =>
        val account = if (command.split("_").length > 1) Some(command.replace("_", ".").stripPrefix("/cbc.")) else None
        val customerF = customerRepo.get(userId)
        val photoF = photoRepo.getRandom(account)
        for {
          customerOpt <- customerF
          photoOpt <- photoF
        } yield {
          photoOpt.flatMap { photo =>
            customerOpt match {
              // TODO: test which one and one of them should being called
              case Some(customer) => customerRepo.update(Customer(userId, userFullName, customer.count + 1))
              case None => customerRepo.insert(Customer(userId, userFullName, 1))
            }
            println(s"[INFO][CBC] chatId: $chatId, id: ${photo.id}, caption: ${photo.caption}")
            sendPhoto(chatId, photo)
          }
        }
      case otherCommand =>
        println("[ERROR] Command not recognized: " + otherCommand)
        Future.successful(None)
    }
  }

}
