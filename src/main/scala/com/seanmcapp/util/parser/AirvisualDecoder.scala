package com.seanmcapp.util.parser

case class AirvisualResponse(status: String, data: AirvisualData)

case class AirvisualData(city: String, current: AirvisualCurrentData)

case class AirvisualCurrentData(pollution: AirvisualPollution)

case class AirvisualPollution(aqius: Int)

trait AirvisualDecoder extends Decoder {

  implicit val AirvisualPollutionFormat = jsonFormat(AirvisualPollution, "aqius")

  implicit val AirvisualCurrentDataFormat = jsonFormat(AirvisualCurrentData, "pollution")
  
  implicit val AirvisualDataFormat = jsonFormat(AirvisualData, "city", "current")

  implicit val AirvisualResponseFormat = jsonFormat(AirvisualResponse, "status", "data")

}
