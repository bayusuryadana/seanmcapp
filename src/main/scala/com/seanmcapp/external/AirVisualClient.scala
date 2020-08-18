package com.seanmcapp.external

import java.net.URLEncoder

import com.seanmcapp.config.AirvisualConf

class AirVisualClient(http: HttpRequestClient) {

  private val airVisualBaseUrl = "https://api.airvisual.com/v2/city"

  private val cities = List(
    AirvisualCity("Indonesia", "Jakarta", "Jakarta"),
    AirvisualCity("Indonesia", "West Java", "Bekasi"),
    AirvisualCity("Indonesia", "West Java", "Depok"),
    AirvisualCity("Singapore", "Singapore", "Singapore"),
    AirvisualCity("Indonesia", "Riau", "Pekanbaru"),
    AirvisualCity("Indonesia", "Central Kalimantan", "Palangkaraya")
  )

  def getCityResults: Map[AirvisualCity, Int] = cities.map(city => getCityAQI(city)).toMap

  private def getCityAQI(city: AirvisualCity): (AirvisualCity, Int) = {

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

}

case class AirvisualResponse(status: String, data: AirvisualData)
case class AirvisualData(city: String, current: AirvisualCurrentData)
case class AirvisualCurrentData(pollution: AirvisualPollution)
case class AirvisualPollution(aqius: Int)
case class AirvisualCity(country: String, state: String, city: String)