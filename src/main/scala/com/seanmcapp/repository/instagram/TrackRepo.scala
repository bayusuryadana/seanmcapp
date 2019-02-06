package com.seanmcapp.repository.instagram

import scala.concurrent.Future

case class Track(customerId: Long, photoId: Long, date:Long)

trait TrackRepo {

  def getAll: Future[Seq[Track]]

  def insert(track: Track): Future[Int]

}
