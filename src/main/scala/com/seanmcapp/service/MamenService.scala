package com.seanmcapp.service

import com.seanmcapp.external.{GeoFilter, GoogleClient, MamenRequest}
import com.seanmcapp.repository.seanmcmamen.{Cities, Stall, StallRepo}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MamenService(stallRepo: StallRepo, googleClient: GoogleClient) {
  
  //$COVERAGE-OFF$
  def fetch(): Future[Seq[Int]] = {
    for {
      stalls <- stallRepo.getAll
      filteredStalls = stalls.filter(stall => stall.latitude.isEmpty || stall.longitude.isEmpty)
      fetchedStalls = filteredStalls.map { stall =>
        val (lat, lng) = googleClient.fetchLatLng(stall.plusCode)
        stall.copy(latitude = lat, longitude = lng)
      }
      updatedStalls <- stallRepo.update(fetchedStalls)
    } yield updatedStalls
  }
  //$COVERAGE-ON$

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
