package com.seanmcapp.service

import com.seanmcapp.repository.seanmcmamen.{Cities, Diner, DinerRepo}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MamenService(dinerRepo: DinerRepo) {
  
  def searchByNameOrDescription(searchTerm: String): Future[Seq[Diner]] = {
    dinerRepo.getAll.map(_.filter { diner =>
      val r = searchTerm.r
      r.findFirstIn(diner.name).isDefined || r.findFirstIn(diner.description).isDefined
    })
  }
  
  def searchByCity(cityId: Int): Future[Seq[Diner]] = dinerRepo.getAll.map(_.filter(_.cityId == Cities.apply(cityId)))
  
  def searchByGeo(lat: Double, long: Double, length: Double): Future[Seq[Diner]] = dinerRepo.getAll.map(_.filter { diner =>
    diner.latitude <= lat + length && diner.latitude >= lat - length && diner.longitude <= long + length && diner.longitude >= long - length
  })
  
}
