package com.seanmcapp.api

import com.seanmcapp.repository._
import com.seanmcapp.util.parser.BroadcastMessage
import com.seanmcapp.util.requestbuilder.TelegramRequest
import spray.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

abstract class WebAPI extends Service {

  import com.seanmcapp.util.parser.WebAPIJson._

  val customerRepo: CustomerRepo
  val photoRepo: PhotoRepo

  def get(request: JsValue): Future[JsValue] = {
    request.asInstanceOf[JsString].value match {
      case "latest" => photoRepo.getLatest.map(_.toJson)
      case "random" => photoRepo.getRandom.map(_.toJson)
      case _ => Future.successful(JsString("no such method"))
    }
  }

  def post(request: JsValue, input: JsValue): Future[JsValue] = {
    request.asInstanceOf[JsString].value match {
      case "random" => randomFlow(input)
      case "vote" => voteFlow(input)
      case "broadcast" => broadcastFlow(input)
      case _ => Future.successful(JsString("no such method"))
    }
  }

  private def randomFlow(input: JsValue): Future[JsValue] = {
    val request = input.convertTo[Customer]
    val customer = Customer(request.id, request.name, request.platform)
    getRandom[Photo](customer, (p:Photo) => p).map(_.toJson)
  }

  private def voteFlow(input: JsValue): Future[JsValue] = {
    val request = input.convertTo[Vote]
    val vote = Vote(request.photoId, request.customerId, request.rating)
    doVote(vote).map(_ => 200.toJson)
  }

  private def broadcastFlow(input: JsValue): Future[JsValue] = {
    val telegramRequest = new TelegramRequest {}
    val request = input.convertTo[BroadcastMessage]
    if (telegramRequest.telegramConf.key == request.key) {
      if (request.recipient == 0) {
        val customerRepoFuture = customerRepo.getAll
        for {
          customerRepo <- customerRepoFuture
        } yield {
          val result = customerRepo.map { subscriber =>
            telegramRequest.getTelegramSendMessege(subscriber.id, request.message).isSuccess
          }.reduce { (a, b) => a && b }
          JsBoolean(result)
        }
      } else {
        // my telegram id = 274852283L
        Future.successful(JsBoolean(telegramRequest.getTelegramSendMessege(request.recipient, request.message).isSuccess))
      }
    } else {
      Future.successful(JsString("wrong key"))
    }
  }

}
