package com.seanmcapp.external

case class GeoFilter(nw: LatLng, se: LatLng)
case class MamenFilter(name: Option[String] = None, cityId: Option[Int] = None, geo: Option[GeoFilter] = None)
case class MamenRequest(filter: MamenFilter)
