package com.seanmcapp.service

import com.seanmcapp.config.{BroadcastConf, TelegramConf}
import com.seanmcapp.helper.HttpRequestBuilder
import com.seanmcapp.model.{BroadcastMessage, Customer, TelegramMessage}
import com.seanmcapp.repository._

import scala.concurrent.ExecutionContext.Implicits.global

object TelegramService extends HttpRequestBuilder {

  private val GROUP = "group"
  private val SUPERGROUP = "supergroup"

  private val ALL = 0

  private val telegramConf = TelegramConf()
  private val broadcastConf = BroadcastConf()

  def flow(request: TelegramMessage) = {
    val customerId = request.chat.id
    val customerName =
      if (request.chat.chatType == GROUP || request.chat.chatType == SUPERGROUP) {
        request.chat.title.get
      } else {
        request.from.firstName + " " + request.from.lastName
      }
    val customerFuture = CustomerRepo.get(customerId).map(_.getOrElse(
      Customer(customerId, customerName, isSubscribed = false, 0)
    ))

    for {
      customer <- customerFuture
    } yield {
      request.entities.map { entity =>
        val command = request.text.substring(entity.offset, entity.offset + entity.length)
          .stripSuffix(telegramConf.botname)
         command match {
          case "/latest" =>
            PhotoRepo.getLatest.map(_.map { photo =>
                sendPhoto(request.chat.id, photo.thumbnailSrc, photo.caption)
            })
          case "/cari_bahan_ciol" =>
            PhotoRepo.getRandom.map(_.map { photo =>
              sendPhoto(request.chat.id, photo.thumbnailSrc, photo.caption)
              CustomerRepo.update(customer.copy(hitCount = customer.hitCount + 1))
            })
          case "/subscribe" => {
            CustomerRepo.update(customer.copy(isSubscribed = true))
            sendMessege(request.chat.id, "selamat berciol ria")
          }
          case "/unsubscribe" => {
            CustomerRepo.update(customer.copy(isSubscribed = false))
            sendMessege(request.chat.id, "yah :( yakin udah puas ciolnya?")
          }
          case _ => new Throwable("No command found ToT")
        }
      }
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
            TelegramService.sendMessege(subscriber.id, request.message)
          }
        }
      } else {
        // uncomment this for dev env
        // my telegram id = 274852283L
        TelegramService.sendMessege(request.recipient, request.message)
      }
      (200, "sent all the message")
    } else {
      (403, "wrong key")
    }
  }

  def sendPhoto(chatId: Long, url: String, caption: String): Int = {
    getTelegramSendPhoto(chatId, url, caption).asString.code
  }

  def sendMessege(chatId: Long, text: String): Int = {
    getTelegramSendMessege(chatId, text).asString.code
  }
}