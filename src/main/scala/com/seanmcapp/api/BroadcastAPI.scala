package com.seanmcapp.api

import com.seanmcapp.repository.CustomerRepo
import com.seanmcapp.util.parser.BroadcastMessage
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder
import spray.json.JsValue
import scala.concurrent.ExecutionContext.Implicits.global

class BroadcastAPI(customerRepo: CustomerRepo) extends API with TelegramRequestBuilder {

  private val ALL = 0

  def flow(input: JsValue): (Int, String) = {
    import com.seanmcapp.util.parser.BroadcastJson._
    val request = input.convertTo[BroadcastMessage]
    if (telegramConf.key == request.key) {
      if (request.recipient == ALL) {
        val customerRepoFuture = customerRepo.getAllSubscribedCust
        for {
          customerRepo <- customerRepoFuture
        } yield {
          customerRepo.map { subscriber =>
            getTelegramSendMessege(subscriber.id, request.message).asString.code
          }
        }
      } else {
        // uncomment this for dev env
        // my telegram id = 274852283L
        getTelegramSendMessege(request.recipient, request.message).asString.code
      }
      (200, "all messages sent")
    } else {
      (403, "wrong key")
    }
  }

}
