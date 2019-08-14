package com.seanmcapp.scheduler

import java.net.URLEncoder

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.config.AirvisualConf
import com.seanmcapp.util.parser.AirvisualResponse
import scalaj.http.Http
import spray.json._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class AirVisualScheduler(startTime: Int, interval: FiniteDuration)
                        (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) {

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

    val cityResults = cities.map(city => getCityResult(city))

    val stringMessage = cityResults.foldLeft("*Seanmcearth* melaporkan kondisi udara saat ini:\n") { (res, data) =>
      res + data
    }

    sendMessage(-1001359004262L, URLEncoder.encode(stringMessage, "UTF-8"))
    println("success")
  }

  private def getCityResult(city: City): String = {
    import com.seanmcapp.util.parser.AirvisualJson._

    val airvisualConf = AirvisualConf()

    val apiParams = "?country=%s&state=%s&city=%s&key=%s"
    val apiUrl = airVisualBaseUrl + apiParams.format(
      URLEncoder.encode(city.country, "UTF-8"),
      URLEncoder.encode(city.state, "UTF-8"),
      URLEncoder.encode(city.city, "UTF-8"),
      airvisualConf.key)

    val response = Http(apiUrl).asString.body.parseJson.convertTo[AirvisualResponse].data
    val aqius = response.current.pollution.aqius

    "\n" + city.city + " (AQI " + aqius + " " + getEmojiFromAqi(aqius) + ")"
  }

  private def getEmojiFromAqi(aqi: Int): String = {
    aqi match {
      case aqi if aqi <= 50 => new String(AirGood, 0, AirGood.length)
      case aqi if aqi > 50 & aqi <= 100 => new String(AirModerate, 0, AirModerate.length)
      case aqi if aqi > 100 & aqi <= 150 => new String(AirSensitive, 0, AirSensitive.length)
      case aqi if aqi > 150 & aqi <= 200 => new String(AirUnhealthy, 0, AirUnhealthy.length)
      case aqi if aqi > 200 => new String(AirRisky, 0, AirRisky.length)
    }
  }

}
