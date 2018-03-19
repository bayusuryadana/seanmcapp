package com.seanmcapp.service

import com.seanmcapp.config.{BroadcastConf, TelegramConf}
import com.seanmcapp.helper.HttpRequestBuilder
import com.seanmcapp.model._
import com.seanmcapp.repository._

import scala.concurrent.ExecutionContext.Implicits.global

object TelegramService extends HttpRequestBuilder {

  private val GROUP = "group"
  private val SUPERGROUP = "supergroup"

  private val ALL = 0

  private val telegramConf = TelegramConf()
  private val broadcastConf = BroadcastConf()

  def flow(request: TelegramUpdate) = {
    request.message.map { message =>
      val customerId = message.chat.id
      val customerName =
        if (message.chat.chatType == GROUP || message.chat.chatType == SUPERGROUP) {
          message.chat.title.get
        } else {
          message.from.firstName + " " + message.from.lastName
        }
      val customerFuture = CustomerRepo.get(customerId).map(_.getOrElse(
        Customer(customerId, customerName, isSubscribed = false, 0)
      ))

      for {
        customer <- customerFuture
      } yield {
        message.entities.map { entity =>
          val command = message.text.substring(entity.offset, entity.offset + entity.length)
            .stripSuffix(telegramConf.botname)
          command match {
            case "/latest" =>
              PhotoRepo.getLatest.map(_.map { photo =>
                getTelegramSendPhoto(telegramConf.endpoint, message.chat.id, photo).asString.code
              })
            case "/cari_bahan_ciol" =>
              PhotoRepo.getRandom.map(_.map { photo =>
                CustomerRepo.update(customer.copy(hitCount = customer.hitCount + 1))
                getTelegramSendPhoto(telegramConf.endpoint, message.chat.id, photo).asString.code
              })
            case "/subscribe" =>
              CustomerRepo.update(customer.copy(isSubscribed = true))
              getTelegramSendMessege(telegramConf.endpoint, message.chat.id, "selamat berciol ria").asString.code
            case "/unsubscribe" =>
              CustomerRepo.update(customer.copy(isSubscribed = false))
              getTelegramSendMessege(telegramConf.endpoint, message.chat.id, "yah :( yakin udah puas ciolnya?").asString.code
            case _ => new Throwable("No command found ToT")
          }
        }
      }
    }

    request.callbackQuery.map { cb =>
      val queryId = cb.id
      val customerId = cb.from.id
      val photoId = cb.data.split(":").head
      val rating = cb.data.split(":").last.toLong

      VoteRepo.update(Vote(customerId + ":" + photoId, photoId, customerId, rating))

      getAnswerCallbackQuery(telegramConf.endpoint, queryId, "Vote received, thank you!")
    }
  }

  def flowBroadcast(request: BroadcastMessage): (Int, String) = {
    if (broadcastConf.key == request.key) {
      if (request.recipient == ALL) {
        val customerRepoFuture = CustomerRepo.getAllSubscribedCust
        for {
          customerRepo <- customerRepoFuture
        } yield {
          customerRepo.map { subscriber =>
            getTelegramSendMessege(telegramConf.endpoint, subscriber.id, request.message).asString.code
          }
        }
      } else {
        // uncomment this for dev env
        // my telegram id = 274852283L
        getTelegramSendMessege(telegramConf.endpoint, request.recipient, request.message).asString.code
      }
      (200, "sent all the message")
    } else {
      (403, "wrong key")
    }
  }
}