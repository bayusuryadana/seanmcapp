package com.seanmcapp.util.requestbuilder

import scalaj.http.{Http, HttpResponse}

trait DotaRequest {

  val baseUrl = "https://api.opendota.com/api/players/"

  def getMatches(id: Long): HttpResponse[String] = {
    Http(baseUrl + id + "/matches").asString
  }

  def getPeers(id: Long): HttpResponse[String] = {
    Http(baseUrl + id + "/peers").asString
  }

}
