package com.seanmcapp.service

import com.seanmcapp.repository.instagram._
import com.seanmcapp.util.parser.{TelegramCallbackQuery, TelegramMessage}
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait CBCService extends TelegramRequestBuilder {

  val customerRepo: CustomerRepo
  val voteRepo: VoteRepo
  val photoRepo: PhotoRepo
  val trackRepo: TrackRepo

  private val TELEGRAM_PLATFORM = "telegram"

  def random(customer: Customer): Future[Option[Photo]] = getRandom(customer, None, None)

  def vote(vote: Vote): Future[Int] = doVote(vote)

  def randomFlow(message: TelegramMessage): Future[Int] = {
    message.entities.getOrElse(Seq.empty).headOption match {
      case Some(entity) =>
        val command = message.text.getOrElse("")
          .substring(entity.offset, entity.offset + entity.length)
          .stripSuffix(telegramConf.botname)

        command.split("_").head match {
          case "/cbc" =>
            val isFromGroup = if (message.chat.chatType == "group" || message.chat.chatType == "supergroup")
              Some(Customer(message.chat.id, message.chat.title.getOrElse(""), TELEGRAM_PLATFORM)) else None
            val customer = Customer(message.from.id, message.from.firstName + " " + message.from.lastName.getOrElse(""), TELEGRAM_PLATFORM)
            val account = if (command.split("_").length > 1) Some(command.replace("_", ".").stripPrefix("/cbc.")) else None
            getRandom(customer, isFromGroup, account).map(_.map { photo =>
              sendPhoto(message.chat.id, photo).code
            }.getOrElse(throw new Exception("photo not found")))
          case _ => throw new Exception("command not found")
        }
      case _ => throw new Exception("Telegram Message Entities not found")
    }
  }

  def voteFlow(cb: TelegramCallbackQuery): Future[Int] = {
    val queryId = cb.id
    val customerId = cb.from.id
    val rating = cb.data.split(":").head.toLong
    val photoId = cb.data.split(":").last.toLong

    val vote = Vote(customerId, photoId, rating)
    doVote(vote).map(_ => sendAnswerCallbackQuery(queryId, "Vote received, thank you!").code)
  }

  private def getRandom(customer: Customer, isFromGroup: Option[Customer], account: Option[String]): Future[Option[Photo]] = {
    photoRepo.getRandom(account).map(_.map { photo =>
      // do tracking
      // update user info
      val (customerF, customerId) = if (isFromGroup.isDefined) {
        val groupCustomer = isFromGroup.get
        (customerRepo.insertOrUpdate(groupCustomer), groupCustomer.id)
      } else {
        (customerRepo.insertOrUpdate(customer), customer.id)
      }
      val track = Track(customerId, photo.id, System.currentTimeMillis / 1000)
      val trackF = trackRepo.insert(track)
      Future.sequence(Seq(customerF, trackF)).map(res => println("[INFO] Track done: " + res))

      photo
    })
  }

  private def doVote(vote: Vote): Future[Int] = {
    voteRepo.insertOrUpdate(vote)
  }

}
