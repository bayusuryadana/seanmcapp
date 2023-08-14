package com.seanmcapp.external

case class GeoFilter(lat: Double, long: Double, length: Double)
case class MamenFilter(name: Option[String] = None, cityId: Option[Int] = None, geo: Option[GeoFilter] = None)
case class MamenRequest(filter: MamenFilter)
