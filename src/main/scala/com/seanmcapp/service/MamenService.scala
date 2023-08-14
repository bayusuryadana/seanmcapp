package com.seanmcapp.service

import com.seanmcapp.external.{GeoFilter, MamenRequest}
import com.seanmcapp.repository.seanmcmamen.{Cities, Stall, StallRepo}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MamenService(stallRepo: StallRepo) {

  def search(request: MamenRequest): Future[Seq[Stall]] = {
    for {
      stalls <- stallRepo.getAll
    } yield {
      stalls.wow(request.filter.name, { (stall, searchTerm: String) => searchTerm.r.findFirstIn(stall.name).isDefined })
        .wow(request.filter.cityId, { (stall, cityId: Int) => stall.cityId == Cities.apply(cityId) })
        .wow(request.filter.geo, { (stall, geo: GeoFilter) =>
          val lat = geo.lat
          val long = geo.long
          val length = geo.length
          if (stall.latitude.isEmpty || stall.longitude.isEmpty)
            false
          else
            stall.latitude.get <= lat + length && stall.latitude.get >= lat - length &&
              stall.longitude.get <= long + length && stall.longitude.get >= long - length
        })
    }
  }

  implicit class ehek(stalls: Seq[Stall]) {
    def wow[T](prerequisites: Option[T], fn: (Stall, T) => Boolean): Seq[Stall] = {
      prerequisites match {
        case Some(x) => stalls.filter(fn(_, x))
        case _ => stalls
      }
    }
  }
}
