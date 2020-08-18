package com.seanmcapp.service

import com.seanmcapp.external.{CBCClient, TelegramClient, TelegramResponse, TelegramUpdate}
import com.seanmcapp.repository.instagram._
import com.seanmcapp.util.MemoryCache
import scalacache.Cache
import scalacache.modes.sync._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CBCService(photoRepo: PhotoRepo, customerRepo: CustomerRepo, cbcClient: CBCClient, telegramClient: TelegramClient) extends MemoryCache {

  implicit val lastPhotoCache: Cache[Long] = createCache[Long]

  def random: Future[Option[Photo]] = photoRepo.getRandom(None)

  def randomFlow(telegramUpdate: TelegramUpdate): Future[Option[TelegramResponse]] = {
    val message = telegramUpdate.message.getOrElse(throw new Exception("This request is does not have a message"))
    val chatId = message.chat.id
    val userId = message.from.id
    val userFullName = message.from.firstName + " " + message.from.lastName.getOrElse("")
    val result = message.entities.getOrElse(List.empty).headOption match {
      case Some(entity) =>
        val command = message.text.getOrElse("")
          .substring(entity.offset, entity.offset + entity.length)
          .stripSuffix(telegramClient.telegramConf.botname)
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

  private[service] def executeCommand(command: String, chatId: Long, userId: Long, userFullName: String): Future[Option[TelegramResponse]] = {
    command.split("_").headOption match {
      case Some(s) if s == "/cbc" =>
        val account = if (command.split("_").length > 1) Some(command.replace("_", ".").stripPrefix("/cbc.")) else None
        val customerF = customerRepo.get(userId)
        val photoF = photoRepo.getRandom(account)
        photoFlow(photoF, customerF, chatId, userId, userFullName)
      case Some(s) if s == "/recommendation" =>
        lastPhotoCache.get(chatId).flatMap { lastPhotoId =>
          cbcClient.getRecommendation.get(lastPhotoId).map { recommendations =>
            val r = scala.util.Random
            val photoId = recommendations(r.nextInt(recommendations.length))
            val customerF = customerRepo.get(userId)
            val photoF = photoRepo.get(photoId)
            photoFlow(photoF, customerF, chatId, userId, userFullName)
          }
        }.getOrElse(Future.successful(None))
      case _ =>
        println("[ERROR] Command not recognized: " + command)
        Future.successful(None)
    }
  }

  private def photoFlow(photoF: Future[Option[Photo]], customerF: Future[Option[Customer]],
                        chatId: Long, userId: Long, userFullName: String): Future[Option[TelegramResponse]] = {
    for {
      customerOpt <- customerF
      photoOpt <- photoF
    } yield {
      photoOpt.map { photo =>
        // Tracking customer
        customerOpt match {
          // TODO: test which one and one of them should being called
          case Some(customer) => customerRepo.update(Customer(userId, userFullName, customer.count + 1))
          case None => customerRepo.insert(Customer(userId, userFullName, 1))
        }

        // Set cache, logging and send photo
        println(s"[INFO][CBC] chatId: $chatId, id: ${photo.id}, caption: ${photo.caption}")
        lastPhotoCache.put(userId)(photo.id)
        val photoId = photo.id
        val url = s"${cbcClient.storageConf.host}/${cbcClient.storageConf.bucket}/cbc/$photoId.jpg"
        val caption = photo.caption + "%0A%40" + photo.account
        telegramClient.sendPhoto(chatId, url, caption)
      }
    }
  }

}
