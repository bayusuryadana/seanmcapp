package com.seanmcapp.util.parser

import spray.json.{DefaultJsonProtocol, JsValue}

case class AmarthaResponse(status: Int, code: Int, message: String, data: JsValue)

case class AmarthaAuthData(accessToken: String, name: String)

case class AmarthaMarketplaceItem(borrowerName: String, creditScoreGrade: String, plafond: Long, revenueProjection: Long, tenor: Int, purpose: String)

case class AmarthaMarketplaceData(total: Int, totalData: Int, totalLatest: Int, marketplace: Seq[AmarthaMarketplaceItem])

object AmarthaJson extends DefaultJsonProtocol {

  implicit val amarthaResponseFormat = jsonFormat(AmarthaResponse, "status", "code", "message", "data")
  implicit val amarthaAuthDataFormat = jsonFormat(AmarthaAuthData, "accessToken", "name")
  implicit val amarthaMarketplaceItemFormat = jsonFormat(AmarthaMarketplaceItem, "borrowerName", "creditScoreGrade", "plafond", "revenueProjection", "tenor", "purpose")
  implicit val amarthaMarketplaceDataFormat = jsonFormat(AmarthaMarketplaceData, "total", "totalData", "totalLatest", "marketplace")

}
