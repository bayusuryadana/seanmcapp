package com.seanmcapp.api

import com.seanmcapp.repository._
import com.seanmcapp.util.parser.{TelegramMessage, TelegramUpdate}
import com.seanmcapp.util.requestbuilder.TelegramRequest
import spray.json._

import scala.concurrent.Future

abstract class TelegramAPI extends Bot with TelegramRequest {

  private val GROUP = "group"
  private val SUPERGROUP = "supergroup"

  def flow(input: JsValue): Future[JsValue] = {
    println("===== INPUT =====\n" + input + "\n")
    import com.seanmcapp.util.parser.TelegramJson._
    val request = input.convertTo[TelegramUpdate]

    request.message.map { message =>
      val customerId = message.chat.id
      val (customerName, userDefault) = if (message.chat.chatType == GROUP || message.chat.chatType == SUPERGROUP) {
        (message.chat.title.get, Some(Customer(message.from.id, getName(message), isSubscribed = false)))
      } else {
        (getName(message), None)
      }
      val customerDefault = Customer(customerId, customerName, isSubscribed = false)

      message.entities.getOrElse(Seq.empty).map { entity =>
        val command = message.text.getOrElse("").substring(entity.offset, entity.offset + entity.length).stripSuffix(telegramConf.botname)
        command match {
          case "/latest" =>
            getLatest(photo => getTelegramSendPhoto(message.chat.id, photo).code)
          case "/cari_bahan_ciol" =>
            getRandom(userDefault, photo => getTelegramSendPhoto(message.chat.id, photo).code)
          case "/subscribe" =>
            subscribe(customerDefault)
            getTelegramSendMessege(message.chat.id, "selamat berciol ria").code
          case "/unsubscribe" =>
            resetCustomer(customerDefault)
            getTelegramSendMessege(message.chat.id, "yah :( yakin udah puas ciolnya?").code
          case _ => 404
        }
      }
    }

    request.callbackQuery.map { cb =>
      val queryId = cb.id
      val customerId = cb.from.id
      val photoId = cb.data.split(":").head
      val rating = cb.data.split(":").last.toLong

      vote(Vote(customerId + ":" + photoId, photoId, customerId, rating))
      getAnswerCallbackQuery(queryId, "Vote received, thank you!").code
    }

    Future.successful(JsNumber(200))
  }

  private def getName(message: TelegramMessage): String = {
    message.from.firstName + " " + message.from.lastName.getOrElse("")
  }

}