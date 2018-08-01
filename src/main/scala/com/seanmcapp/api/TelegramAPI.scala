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
      message.entities.getOrElse(Seq.empty).map { entity =>
        val command = message.text.getOrElse("")
          .substring(entity.offset, entity.offset + entity.length)
          .stripSuffix(telegramConf.botname)

        command.split("_").head match {
          case "/cbc" =>
            val userDefault = if (message.chat.chatType == GROUP || message.chat.chatType == SUPERGROUP)
              Some(Customer(message.from.id, getName(message))) else None

            if (command.split("_").length > 1) {
              val account = command.replace("_", ".").stripPrefix("/cbc.")
              getRandom(account, userDefault, photo => getTelegramSendPhoto(message.chat.id, photo).code)
            } else {
              getRandom(userDefault, photo => getTelegramSendPhoto(message.chat.id, photo).code)
            }
          case _ => 404
        }
      }
    }

    request.callbackQuery.map { cb =>
      val queryId = cb.id
      val customerId = cb.from.id
      val rating = cb.data.split(":").head.toLong
      val photoId = cb.data.split(":").last

      vote(Vote(customerId + ":" + photoId, photoId, customerId, rating))
      getAnswerCallbackQuery(queryId, "Vote received, thank you!").code
    }

    Future.successful(JsNumber(200))
  }

  private def getName(message: TelegramMessage): String = {
    message.from.firstName + " " + message.from.lastName.getOrElse("")
  }

}