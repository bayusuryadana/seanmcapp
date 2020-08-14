package com.seanmcapp.scheduler

import java.net.URLEncoder

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.fetcher.{AirVisualFetcher, AirvisualCity}
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.{Duration, FiniteDuration}

class AirVisualScheduler(startTime: Int, interval: FiniteDuration, airVisualFetcher: AirVisualFetcher)
                        (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) with TelegramRequestBuilder {

  private val AirGood = Array(0x1F340)
  private val AirModerate = Array(0x1F60E)
  private val AirSensitive = Array(0x1F630)
  private val AirUnhealthy = Array(0x1F637)
  private val AirRisky = Array(0x1F480)

  override def task: Map[AirvisualCity, Int] = {
    println("=== AirVisual check ===")

    val cityResults = Await.result(airVisualFetcher.getCityResults, Duration.Inf)

    val stringMessage = cityResults.foldLeft("*Seanmcearth* melaporkan kondisi udara saat ini:") { (res, row) =>
      val city = row._1
      val aqius = row._2
      val appendString = "\n" + city.city + " (AQI " + aqius + " " + getEmojiFromAqi(aqius) + ")"
      res + appendString
    }
    sendMessage(-1001359004262L, URLEncoder.encode(stringMessage, "UTF-8"))
    cityResults
  }

  private def getEmojiFromAqi(aqi: Int): String = {
    aqi match {
      case _ if aqi <= 50 => new String(AirGood, 0, AirGood.length)
      case _ if aqi > 50 & aqi <= 100 => new String(AirModerate, 0, AirModerate.length)
      case _ if aqi > 100 & aqi <= 150 => new String(AirSensitive, 0, AirSensitive.length)
      case _ if aqi > 150 & aqi <= 200 => new String(AirUnhealthy, 0, AirUnhealthy.length)
      case _ if aqi > 200 => new String(AirRisky, 0, AirRisky.length)
    }
  }

}
