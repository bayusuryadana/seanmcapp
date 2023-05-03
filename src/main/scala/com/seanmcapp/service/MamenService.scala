package com.seanmcapp.service

import com.seanmcapp.repository.seanmcmamen.{Cities, Stall, StallRepo}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MamenService(stallRepo: StallRepo) {
  
  def searchByNameOrDescription(searchTerm: String): Future[Seq[Stall]] = {
    stallRepo.getAll.map(_.filter { stall =>
      val r = searchTerm.r
      r.findFirstIn(stall.name).isDefined || r.findFirstIn(stall.description).isDefined
    })
  }
  
  def searchByCity(cityId: Int): Future[Seq[Stall]] = stallRepo.getAll.map(_.filter(_.cityId == Cities.apply(cityId)))
  
  def searchByGeo(lat: Double, long: Double, length: Double): Future[Seq[Stall]] = stallRepo.getAll.map(_.filter { stall =>
    stall.latitude <= lat + length && stall.latitude >= lat - length && stall.longitude <= long + length && stall.longitude >= long - length
  })
  
}
