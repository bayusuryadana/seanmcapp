package com.seanmcapp.scheduler

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.util.requestbuilder.{HttpRequestBuilder, TelegramRequestBuilder}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class NCovScheduler(startTime: Int, interval: FiniteDuration, override val http: HttpRequestBuilder)
                   (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) with TelegramRequestBuilder  {

  override def task: Any = {
    val urls = List(
      "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/time_series/time_series_2019-ncov-Confirmed.csv",
      "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/time_series/time_series_2019-ncov-Deaths.csv",
      "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/time_series/time_series_2019-ncov-Recovered.csv"
    )

    val results = urls.map { u =>
      http.sendGetRequest(u)
        .split("\n")
        .collect { case l if l.contains("Singapore") => l.split(",").last }
        .head
    }

    val result = s"Singapore case Confirmed: ${results(0)}, Death: ${results(1)}, Recovered: ${results(2)}"
    sendMessage(-1001359004262L, result)
    result
  }
}
