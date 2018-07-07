package com.seanmcapp.api

import com.seanmcapp.repository.CustomerRepo
import com.seanmcapp.util.parser.BroadcastMessage
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BroadcastAPI(customerRepo: CustomerRepo) extends API with TelegramRequestBuilder {

  private val ALL = 0

  def flow(input: JsValue): Future[JsValue] = {
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
          // uncomment this for dev env
          // my telegram id = 274852283L
          Future.successful(JsBoolean(getTelegramSendMessege(request.recipient, request.message).asString.isSuccess))
        }
      } else {
        Future.successful(JsString("wrong key"))
      }
  }

}
