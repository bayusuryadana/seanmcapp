package com.seanmcapp.api

import com.seanmcapp.repository.{Customer, Photo, PhotoRepo}
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait API extends DefaultJsonProtocol {

  val photoRepo: PhotoRepo

  def getLatest(callback: Photo => Int): Future[Option[Int]] = {
    photoRepo.getLatest.map(_.map(callback))
  }

  def getRandom(user: Option[Customer], callback: Photo => Int): Future[Option[Int]] = {
    photoRepo.getRandom.map(_.map(callback))
  }

}