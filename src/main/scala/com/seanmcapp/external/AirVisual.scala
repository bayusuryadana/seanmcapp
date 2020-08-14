package com.seanmcapp.external

import java.net.URLEncoder

import com.seanmcapp.config.AirvisualConf

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AirVisual(http: HttpRequestClient) {

  private[external] val airVisualBaseUrl = "https://api.airvisual.com/v2/city"

  private val cities = List(
    AirvisualCity("Indonesia", "Jakarta", "Jakarta"),
    AirvisualCity("Indonesia", "West Java", "Bekasi"),
    AirvisualCity("Indonesia", "West Java", "Depok"),
    AirvisualCity("Singapore", "Singapore", "Singapore"),
    AirvisualCity("Indonesia", "Riau", "Pekanbaru"),
    AirvisualCity("Indonesia", "Central Kalimantan", "Palangkaraya")
  )

  def getCityResults: Future[Map[AirvisualCity, Int]] = {
    val citiesF = cities.map(city => getCityAQI(city))
    Future.sequence(citiesF).map(_.flatten.toMap)
  }

  private def getCityAQI(city: AirvisualCity): Future[Option[(AirvisualCity, Int)]] = {

    val airvisualConf = AirvisualConf()

    val apiParams = "?country=%s&state=%s&city=%s&key=%s"
    val apiUrl = airVisualBaseUrl + apiParams.format(
      URLEncoder.encode(city.country, "UTF-8"),
      URLEncoder.encode(city.state, "UTF-8"),
      URLEncoder.encode(city.city, "UTF-8"),
      airvisualConf.key)

    val responseF = http.sendRequest(apiUrl)
    decode[AirvisualResponse](responseF).map {
      case Right(airVisualResponse) =>
        Some((city, airVisualResponse.data.current.pollution.aqius))
      case Left(message) =>
        println(s"[ERROR] $message")
        None
    }
  }

}

case class AirvisualResponse(status: String, data: AirvisualData)
case class AirvisualData(city: String, current: AirvisualCurrentData)
case class AirvisualCurrentData(pollution: AirvisualPollution)
case class AirvisualPollution(aqius: Int)
case class AirvisualCity(country: String, state: String, city: String)