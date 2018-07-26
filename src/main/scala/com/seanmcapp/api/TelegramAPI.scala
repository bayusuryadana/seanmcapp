package com.seanmcapp.api

import com.seanmcapp.repository._
import com.seanmcapp.util.parser.TelegramUpdate
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TelegramAPI(customerRepo: CustomerRepo, photoRepo: PhotoRepo, voteRepo: VoteRepo) extends API with TelegramRequestBuilder {

  private val GROUP = "group"
  private val SUPERGROUP = "supergroup"

  def flow(input: JsValue): Future[JsValue] = {
    println("===== INPUT =====\n" + input + "\n")
    import com.seanmcapp.util.parser.TelegramJson._
    val request = input.convertTo[TelegramUpdate]

    request.message.map { message =>
      val customerId = message.chat.id
      val customerName =
        if (message.chat.chatType == GROUP || message.chat.chatType == SUPERGROUP) {
          message.chat.title.get
        } else {
          message.from.firstName + " " + message.from.lastName.getOrElse("")
        }
      val customerFuture = customerRepo.get(customerId).map(_.getOrElse(
        Customer(customerId, customerName, isSubscribed = false, 0)
      ))

      for {
        customer <- customerFuture
      } yield {
        message.entities.getOrElse(Seq.empty).map { entity =>
          val command = message.text.getOrElse("").substring(entity.offset, entity.offset + entity.length).stripSuffix(telegramConf.botname)
          command match {
            case "/latest" =>
              photoRepo.getLatest.map(_.map { photo =>
                getTelegramSendPhoto(message.chat.id, photo).asString.code
              })
            case "/cari_bahan_ciol" =>
              photoRepo.getRandom.map(_.map { photo =>
                customerRepo.update(customer.copy(hitCount = customer.hitCount + 1))
                getTelegramSendPhoto(message.chat.id, photo).asString.code
              })
            case "/subscribe" =>
              customerRepo.update(customer.copy(isSubscribed = true))
              getTelegramSendMessege(message.chat.id, "selamat berciol ria").asString.code
            case "/unsubscribe" =>
              customerRepo.update(customer.copy(isSubscribed = false))
              getTelegramSendMessege(message.chat.id, "yah :( yakin udah puas ciolnya?").asString.code
            case _ => 404
          }
        }
      }
    }

    request.callbackQuery.map { cb =>
      val queryId = cb.id
      val customerId = cb.from.id
      val photoId = cb.data.split(":").head
      val rating = cb.data.split(":").last.toLong

      voteRepo.update(Vote(customerId + ":" + photoId, photoId, customerId, rating))
      getAnswerCallbackQuery(queryId, "Vote received, thank you!").asString.code
    }

    Future.successful(JsNumber(200))
  }
}