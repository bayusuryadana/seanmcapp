package com.seanmcapp.external

import com.seanmcapp.util.{DsdaJakartaDecoder, DsdaWaterGateResponse}

import scala.xml.XML

class DsdaJakartaClient(http: HttpRequestClient) extends DsdaJakartaDecoder {

  private val DSDA_JAKARTA_URL = "http://poskobanjirdsda.jakarta.go.id/xmldata.xml"

  def getReport:DsdaWaterGateResponse = {
    val response = http.sendGetRequest(DSDA_JAKARTA_URL)
    decode(XML.loadString(response))
  }

}
