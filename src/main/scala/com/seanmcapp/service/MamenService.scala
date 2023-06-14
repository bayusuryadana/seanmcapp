package com.seanmcapp.service

import com.seanmcapp.repository.seanmcmamen.{Cities, Stall, StallRepo}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MamenService(stallRepo: StallRepo) {
  
  def searchByName(searchTerm: String): Future[Seq[Stall]] = {
    stallRepo.getAll.map(_.filter(stall => searchTerm.r.findFirstIn(stall.name).isDefined))
  }
  
  def searchByCity(cityId: Int): Future[Seq[Stall]] = stallRepo.getAll.map(_.filter(_.cityId == Cities.apply(cityId)))
  
  def searchByGeo(lat: Double, long: Double, length: Double): Future[Seq[Stall]] = {
    stallRepo.getAll.map(_.filter { stall =>
      if (stall.latitude.isEmpty || stall.longitude.isEmpty) 
        false 
      else
        stall.latitude.get <= lat + length && stall.latitude.get >= lat - length && 
          stall.longitude.get <= long + length && stall.longitude.get >= long - length
    })
  }
  
}
