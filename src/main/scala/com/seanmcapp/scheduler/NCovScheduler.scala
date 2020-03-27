package com.seanmcapp.scheduler

import java.net.URLEncoder

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
      "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv",
      "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv",
      "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv"
    )

    val resp = urls.map {u => http.sendGetRequest(u)}

    val sgResults = resp.map { u =>
      u.split("\n").collect { case l if l.contains("Singapore") => l.split(",").last }.head
    }

    val idResults = resp.map { u =>
      u.split("\n").collect { case l if l.contains("Indonesia") => l.split(",").last }.head
    }

    val result = s"Singapore case Confirmed: ${sgResults(0)}, Death: ${sgResults(1)}, Recovered: ${sgResults(2)}\nIndonesia case Confirmed: ${idResults(0)}, Death: ${idResults(1)}, Recovered: ${idResults(2)}"
    sendMessage(-1001359004262L, URLEncoder.encode(result, "UTF-8"))
    result
  }
}
