package com.seanmcapp.util.parser

import spray.json.DefaultJsonProtocol

case class AirvisualResponse(status: String, data: AirvisualData)

case class AirvisualData(city: String, current: AirvisualCurrentData)

case class AirvisualCurrentData(pollution: AirvisualPollution)

case class AirvisualPollution(aqius: Int)

object AirvisualJson extends DefaultJsonProtocol {

  implicit val AirvisualPollutionFormat = jsonFormat(AirvisualPollution, "aqius")

  implicit val AirvisualCurrentDataFormat = jsonFormat(AirvisualCurrentData, "pollution")
  
  implicit val AirvisualDataFormat = jsonFormat(AirvisualData, "city", "current")

  implicit val AirvisualResponseFormat = jsonFormat(AirvisualResponse, "status", "data")

}
