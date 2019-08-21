package com.seanmcapp.scheduler

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.config.SchedulerConf
import com.seanmcapp.util.parser.{IgrowData, IgrowDecoder, IgrowResponse}
import com.seanmcapp.util.requestbuilder.{HttpRequestBuilder, TelegramRequestBuilder}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class IGrowScheduler(startTime: Int, interval: FiniteDuration, override val http: HttpRequestBuilder)
                    (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) with IgrowDecoder with TelegramRequestBuilder {

  private val iGrowBaseUrl = "https://igrow.asia/api/public/en/v1/sponsor/seed"

  override def task: Seq[IgrowData] = {
    println("=== iGrow check ===")
    val response = http.sendRequest(iGrowBaseUrl + "/list")
    val igrowResponse = decode[IgrowResponse](response).data.filter(_.stock > 0)
    println("[INFO][IGROW] number of stock: " + response.length)
    val schedulerConf = SchedulerConf()
    igrowResponse.foreach { data =>
      val stringMessage = s"${data.name}%0A${data.price}%0Asisa ${data.stock} unit"
      schedulerConf.igrow.foreach(chatId => sendMessage(chatId, stringMessage))
    }
    igrowResponse
  }

}