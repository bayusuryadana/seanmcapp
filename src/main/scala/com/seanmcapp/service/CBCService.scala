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

  def random: Future[Option[Photo]] = photoRepo.getRandom

  def randomFlow(telegramUpdate: TelegramUpdate): Future[Option[TelegramResponse]] = {
    val message = telegramUpdate.message.getOrElse(throw new Exception("This request is does not have a message"))
    val chatId = message.chat.id
    val userId = message.from.id
    val userFullName = message.from.first_name + " " + message.from.last_name.getOrElse("")
    val result = message.entities.getOrElse(List.empty).headOption match {
      case Some(entity) =>
        val command = message.text.getOrElse("")
          .substring(entity.offset, entity.offset + entity.length)
          .stripSuffix(telegramClient.telegramConf.botname)
        def sendPhoto(photo: Photo): TelegramResponse = {
          val caption = photo.caption + "%0A%40" + photo.account
          telegramClient.sendPhoto(chatId, getPhotoUrl(photo.id), caption)
        }
        command.split("_").headOption match {
          case Some(s) if s == "/cbc" =>
            cbcFlow(userId, userFullName, "cbc").map(_.map(sendPhoto))
          case Some(s) if s == "/recommendation" =>
            cbcFlow(userId, userFullName, "recommendation").map(_.map(sendPhoto))
          case _ =>
            println("[ERROR] Command not recognized: " + command)
            Future.successful(None)
        }

      case _ =>
        println("[ERROR] No entities (command) found")
        Future.successful(None)
    }

    // logging group chat :D
    if (message.chat.`type` != "private")
      println(s"[CHAT] $userFullName@${message.chat.title.getOrElse("???")}:${message.text.getOrElse("???")}")

    result
  }

  def cbcFlow(userId: Long, userFullName: String, `type`: String): Future[Option[Photo]] = {
    val photoF = `type` match {
      case "cbc" => photoRepo.getRandom
      case "recommendation" =>
        lastPhotoCache.get(userId).flatMap { lastPhotoId =>
          cbcClient.getRecommendation.get(lastPhotoId).map { recommendations =>
            val r = scala.util.Random
            val photoId = recommendations(r.nextInt(recommendations.length))
            photoRepo.get(photoId)
          }
        }.getOrElse(Future.successful(None))
      case _ => throw new Exception("flow type not recognized")
    }

    for {
      customerOpt <- customerRepo.get(userId)
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
        lastPhotoCache.put(userId)(photo.id)
        photo
      }
    }
  }

  def getPhotoUrl(photoId: Long) = s"${cbcClient.storageConf.host}/${cbcClient.storageConf.bucket}/cbc/$photoId.jpg"

}
