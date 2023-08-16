package com.seanmcapp.external

import com.seanmcapp.GoogleConf

import java.net.URLEncoder

case class GeocodeGeometry(location: LatLng)
case class GeocodeResult(geometry: GeocodeGeometry)
case class GeocodeResponse(results: List[GeocodeResult])
class GoogleClient(httpClient: HttpRequestClient) {
  
  private val apiKey = GoogleConf.apply().key
  
  def fetchLatLng(plusCode: String): (Option[Double], Option[Double]) = {
    val url = s"https://maps.googleapis.com/maps/api/geocode/json?key=$apiKey&address=${URLEncoder.encode(plusCode, "UTF-8")}"
    val response = httpClient.sendGetRequest(url)
    decode[GeocodeResponse](response).results.headOption.map { geo =>
      (geo.geometry.location.lat, geo.geometry.location.lng)
    }.unzip
  }

}
