package com.seanmcapp.scheduler

import java.net.URLEncoder

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.external.{AirVisual, FutureUtils, TelegramClient}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class AirVisualScheduler(startTime: Int, interval: FiniteDuration, airVisual: AirVisual, telegramClient: TelegramClient)
                        (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) {

  private val AirGood = Array(0x1F340)
  private val AirModerate = Array(0x1F60E)
  private val AirSensitive = Array(0x1F630)
  private val AirUnhealthy = Array(0x1F637)
  private val AirRisky = Array(0x1F480)

  override def task: Unit = {
    println("=== AirVisual check ===")

    val cityResults = FutureUtils.await(airVisual.getCityResults)

    val stringMessage = cityResults.foldLeft("*Seanmcearth* melaporkan kondisi udara saat ini:") { (res, row) =>
      val city = row._1
      val aqius = row._2
      val appendString = "\n" + city.city + " (AQI " + aqius + " " + getEmojiFromAqi(aqius) + ")"
      res + appendString
    }
    telegramClient.sendMessage(-1001359004262L, URLEncoder.encode(stringMessage, "UTF-8"))
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
