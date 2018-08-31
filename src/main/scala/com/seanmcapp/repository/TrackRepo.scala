package com.seanmcapp.repository

import org.mongodb.scala.Completed
import org.mongodb.scala.bson.annotations.BsonProperty

import scala.concurrent.Future

case class Track(@BsonProperty("customers_id") customerId: Long, @BsonProperty("photos_id") photoId: Long, date:Long)

trait TrackRepo {

  def getAll: Future[Seq[Track]]

  def getLast100(customerId: Long): Future[Set[Long]]

  def insert(track: Track): Future[Completed]

}
