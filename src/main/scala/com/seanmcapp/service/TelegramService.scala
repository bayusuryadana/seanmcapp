package com.seanmcapp.service

import com.seanmcapp.repository.instagram.{Customer, Vote}
import com.seanmcapp.util.parser.{TelegramCallbackQuery, TelegramMessage, TelegramUpdate}
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder
import spray.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait TelegramService extends CBCService with TelegramRequestBuilder {

  private val TELEGRAM_PLATFORM = "telegram"

  def flow(input: JsValue): Future[JsValue] = {
    println("===== INPUT (TELEGRAM) =====\n" + input + "\n")
    import com.seanmcapp.util.parser.TelegramJson._
    val request = input.convertTo[TelegramUpdate]

    val statusCodeF = request match {
      case r if request.message.isDefined => cbcFlow(r.message.get)
      case r if request.callbackQuery.isDefined => voteFlow(r.callbackQuery.get)
      case _ => Future.successful(404)
    }

    statusCodeF.map(n => JsNumber(n))
  }

  private def cbcFlow(message: TelegramMessage): Future[Int] = {
    val request = message.entities.getOrElse(Seq.empty).headOption.map { entity =>
      val command = message.text.getOrElse("")
        .substring(entity.offset, entity.offset + entity.length)
        .stripSuffix(telegramConf.botname)

      val result = command.split("_").head match {
        case "/cbc" =>
          val isFromGroup = if (message.chat.chatType == "group" || message.chat.chatType == "supergroup")
            Some(Customer(message.chat.id, message.chat.title.getOrElse(""), TELEGRAM_PLATFORM)) else None
          val customer = Customer(message.from.id, getName(message), TELEGRAM_PLATFORM)
          val account = if (command.split("_").length > 1) Some(command.replace("_", ".").stripPrefix("/cbc.")) else None
          getRandom(customer, isFromGroup, account) { photo =>
            sendPhoto(message.chat.id, photo).code
          }
        case _ => Future.successful(None)
      }

      result.map(_.getOrElse(404))
    }

    request.getOrElse(Future.successful(404))
  }

  private def voteFlow(cb: TelegramCallbackQuery): Future[Int] = {
    val queryId = cb.id
    val customerId = cb.from.id
    val rating = cb.data.split(":").head.toLong
    val photoId = cb.data.split(":").last.toLong

    val vote = Vote(customerId, photoId, rating)
    doVote(vote).map(_ => sendAnswerCallbackQuery(queryId, "Vote received, thank you!").code)
  }

  private def getName(message: TelegramMessage): String = {
    message.from.firstName + " " + message.from.lastName.getOrElse("")
  }

}
