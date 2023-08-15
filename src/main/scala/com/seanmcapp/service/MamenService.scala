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
      stalls.doFilter(request.filter.name, { (stall, searchTerm: String) => searchTerm.r.findFirstIn(stall.name).isDefined })
        .doFilter(request.filter.cityId, { (stall, cityId: Int) => stall.cityId == Cities.apply(cityId) })
        .doFilter(request.filter.geo, { (stall, geo: GeoFilter) =>
          if (stall.latitude.isEmpty || stall.longitude.isEmpty)
            false
          else
            stall.latitude.get <= geo.nw.lat && stall.latitude.get >= geo.se.lat &&
              stall.longitude.get <= geo.nw.lng && stall.longitude.get >= geo.se.lng
        })
    }
  }

  implicit class stallListHelper(stalls: Seq[Stall]) {
    def doFilter[T](prerequisites: Option[T], fn: (Stall, T) => Boolean): Seq[Stall] = {
      prerequisites match {
        case Some(x) => stalls.filter(fn(_, x))
        case _ => stalls
      }
    }
  }
}
