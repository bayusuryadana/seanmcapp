package com.seanmcapp.scheduler

import java.net.URLEncoder

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.config.AirvisualConf
import com.seanmcapp.util.parser.{AirvisualDecoder, AirvisualResponse}
import com.seanmcapp.util.requestbuilder.{HttpRequestBuilder, TelegramRequestBuilder}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class AirVisualScheduler(startTime: Int, interval: FiniteDuration, override val http: HttpRequestBuilder)
                        (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) with AirvisualDecoder with TelegramRequestBuilder {

  private val airVisualBaseUrl = "https://api.airvisual.com/v2/city"

  private case class City(country: String, state: String, city: String)

  private val AirGood = Array(0x1F340)
  private val AirModerate = Array(0x1F60E)
  private val AirSensitive = Array(0x1F630)
  private val AirUnhealthy = Array(0x1F637)
  private val AirRisky = Array(0x1F480)

  private val cities = List(
    City("Indonesia", "Jakarta", "Jakarta"),
    City("Indonesia", "West Java", "Bekasi"),
    City("Indonesia", "West Java", "Depok"),
    City("Singapore", "Singapore", "Singapore")
  )

  override def task: Unit = {
    println("=== AirVisual check ===")

    val cityResults = cities.map(city => getCityAQI(city)).toMap

    val stringMessage = cityResults.foldLeft("*Seanmcearth* melaporkan kondisi udara saat ini:") { (res, row) =>
      val city = row._1
      val aqius = row._2
      val appendString = "\n" + city.city + " (AQI " + aqius + " " + getEmojiFromAqi(aqius) + ")"
      res + appendString
    }

    sendMessage(-1001359004262L, URLEncoder.encode(stringMessage, "UTF-8"))
  }

  private def getCityAQI(city: City): (City, Int) = {

    val airvisualConf = AirvisualConf()

    val apiParams = "?country=%s&state=%s&city=%s&key=%s"
    val apiUrl = airVisualBaseUrl + apiParams.format(
      URLEncoder.encode(city.country, "UTF-8"),
      URLEncoder.encode(city.state, "UTF-8"),
      URLEncoder.encode(city.city, "UTF-8"),
      airvisualConf.key)

    val response = http.sendRequest(apiUrl)
    val airVisualResponse = decode[AirvisualResponse](response)
    (city, airVisualResponse.data.current.pollution.aqius)
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
