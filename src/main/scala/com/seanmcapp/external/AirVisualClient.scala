package com.seanmcapp.external

import java.net.URLEncoder

import com.seanmcapp.config.AirvisualConf

class AirVisualClient(http: HttpRequestClient) {

  private val airVisualBaseUrl = "https://api.airvisual.com/v2/city"

  private val cities = List(
    AirvisualCity("Indonesia", "Jakarta", "Jakarta"),
    AirvisualCity("Indonesia", "West Java", "Bekasi"),
    AirvisualCity("Indonesia", "West Java", "Depok"),
    AirvisualCity("Singapore", "Singapore", "Singapore")
  )

  def getCityResults: Map[AirvisualCity, Int] = cities.map { city =>
    val airvisualConf = AirvisualConf()
    val apiUrl = s"$airVisualBaseUrl?country=${city.country}&state=${city.state}&city=${city.city}&key=${airvisualConf.key}"

    val response = http.sendGetRequest(URLEncoder.encode(apiUrl, "UTF-8"))
    val airVisualResponse = decode[AirvisualResponse](response)
    (city, airVisualResponse.data.current.pollution.aqius)
  }.toMap

}

case class AirvisualResponse(status: String, data: AirvisualData)
case class AirvisualData(city: String, current: AirvisualCurrentData)
case class AirvisualCurrentData(pollution: AirvisualPollution)
case class AirvisualPollution(aqius: Int)
case class AirvisualCity(country: String, state: String, city: String)