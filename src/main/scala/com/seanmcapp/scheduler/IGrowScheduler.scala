package com.seanmcapp.scheduler

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.config.SchedulerConf
import com.seanmcapp.util.parser.{IgrowData, IgrowResponse}
import scalaj.http.Http
import spray.json._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class IGrowScheduler(startTime: Int, interval: FiniteDuration)
                    (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) {

  private val iGrowBaseUrl = "https://igrow.asia/api/public/en/v1/sponsor/seed"

  override def task: Seq[IgrowData] = {
    println("=== iGrow check ===")
    import com.seanmcapp.util.parser.IgrowJson._
    val response = Http(iGrowBaseUrl + "/list").asString.body.parseJson.convertTo[IgrowResponse].data.filter(_.stock > 0)
    println("[INFO][IGROW] number of stock: " + response.size)
    val schedulerConf = SchedulerConf()
    response.foreach { data =>
      val stringMessage = s"${data.name}%0A${data.price}%0Asisa ${data.stock} unit"
      schedulerConf.igrow.foreach(chatId => sendMessage(chatId, stringMessage))
    }
    response
  }

}