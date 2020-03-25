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
      "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv",
      "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Deaths.csv",
      "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Recovered.csv"
    )

    val sgResults = urls.map { u =>
      http.sendGetRequest(u)
        .split("\n")
        .collect { case l if l.contains("Singapore") => l.split(",").last }
        .head
    }

    val idResults = urls.map { u =>
      http.sendGetRequest(u)
        .split("\n")
        .collect { case l if l.contains("Indonesia") => l.split(",").last }
        .head
    }

    val result = s"Singapore case Confirmed: ${sgResults(0)}, Death: ${sgResults(1)}, Recovered: ${sgResults(2)}\nIndonesia case Confirmed: ${idResults(0)}, Death: ${idResults(1)}, Recovered: ${idResults(2)}"
    sendMessage(-1001359004262L, result)
    result
  }
}
