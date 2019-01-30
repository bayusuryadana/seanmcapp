package com.seanmcapp.repository

import scala.concurrent.Future

case class Track(customerId: Long, photoId: Long, date:Long)

trait TrackRepo {

  def getAll: Future[Seq[Track]]

  def update(track: Track): Future[Option[Track]]

}
