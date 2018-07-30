package com.seanmcapp.api

import com.seanmcapp.repository.{CustomerRepo, Photo, PhotoRepo}
import com.seanmcapp.util.parser.BroadcastMessage
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder
import spray.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

abstract class WebAPI extends API with TelegramRequestBuilder {

  val customerRepo: CustomerRepo

  implicit val photoFormat = jsonFormat5(Photo)
  private val ALL = 0

  def flow(request: JsValue, input: JsValue = JsObject()): Future[JsValue] = {
    request.asInstanceOf[JsString].value match {
      case "latest" => photoRepo.getLatest.map(_.toJson)
      case "random" => photoRepo.getRandom.map(_.toJson)
      case "broadcast" => broadcastFlow(input)
      case _ => Future.successful(JsString("no such method"))
    }
  }

  private def broadcastFlow(input: JsValue): Future[JsValue] = {
    import com.seanmcapp.util.parser.BroadcastJson._
    val request = input.convertTo[BroadcastMessage]
    if (telegramConf.key == request.key) {
      if (request.recipient == ALL) {
        val customerRepoFuture = customerRepo.getAllSubscribedCust
        for {
          customerRepo <- customerRepoFuture
        } yield {
          val result = customerRepo.map { subscriber =>
            getTelegramSendMessege(subscriber.id, request.message).asString.isSuccess
          }.reduce { (a, b) => a && b }
          JsBoolean(result)
        }
      } else {
        // my telegram id = 274852283L
        Future.successful(JsBoolean(getTelegramSendMessege(request.recipient, request.message).asString.isSuccess))
      }
    } else {
      Future.successful(JsString("wrong key"))
    }
  }

}
