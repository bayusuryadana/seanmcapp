package com.seanmcapp.service

import java.util.concurrent.TimeUnit

import com.seanmcapp.config.StorageConf
import com.seanmcapp.repository.instagram._
import com.seanmcapp.util.cache.MemoryCache
import com.seanmcapp.util.parser.decoder.{TelegramInputDecoder, TelegramUpdate}
import com.seanmcapp.util.parser.encoder.{TelegramOutputEncoder, TelegramResponse}
import com.seanmcapp.util.requestbuilder.{HttpRequestBuilder, TelegramRequestBuilder}
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._
import spray.json.JsValue

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class CBCService(photoRepo: PhotoRepo, customerRepo: CustomerRepo, override val http: HttpRequestBuilder)
  extends TelegramRequestBuilder with TelegramInputDecoder with TelegramOutputEncoder with MemoryCache {

  implicit val recommendationCache = createCache[Map[Long, Array[Long]]]
  implicit val lastPhotoCache = createCache[Long]
  val duration = Duration(1, TimeUnit.DAYS)

  private[service] val storageConf = StorageConf()

  def random: Future[Option[Photo]] = photoRepo.getRandom(None)

  def randomFlow(payload: JsValue): Future[Option[TelegramResponse]] = {
    val update = decode[TelegramUpdate](payload)
    val message = update.message.getOrElse(throw new Exception("This request is does not have a message: " + payload))
    val chatId = message.chat.id
    val userId = message.from.id
    val userFullName = message.from.firstName + " " + message.from.lastName.getOrElse("")
    val result = message.entities.getOrElse(List.empty).headOption match {
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

  private[service] def executeCommand(command: String, chatId: Long, userId: Long, userFullName: String): Future[Option[TelegramResponse]] = {
    command.split("_").headOption match {
      case Some(s) if s == "/cbc" =>
        val account = if (command.split("_").length > 1) Some(command.replace("_", ".").stripPrefix("/cbc.")) else None
        val customerF = customerRepo.get(userId)
        val photoF = photoRepo.getRandom(account)
        photoFlow(photoF, customerF, chatId, userId, userFullName)
      case Some(s) if s == "/recommendation" =>
        lastPhotoCache.get(chatId).flatMap { lastPhotoId =>
          getRecommendation.get(lastPhotoId).map { recommendations =>
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

  private[service] def getRecommendation: Map[Long, Array[Long]] = {
    memoizeSync(Some(duration)) {
      val filename = "knn.csv"
      val url = s"${storageConf.host}/${storageConf.bucket}/$filename"
      http.sendGetRequest(url).split("\n").map { line =>
        val row = line.split(",")
        row.head.toLong -> row.tail.map(_.toLong)
      }.toMap
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
        val url = s"${storageConf.host}/${storageConf.bucket}/cbc/$photoId.jpg"
        val caption = photo.caption + "%0A%40" + photo.account
        sendPhoto(chatId, url, caption)
      }
    }
  }

}
