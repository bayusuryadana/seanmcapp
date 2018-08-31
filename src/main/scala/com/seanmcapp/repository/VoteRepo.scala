package com.seanmcapp.repository

import org.mongodb.scala.bson.annotations.BsonProperty

import scala.concurrent.Future

case class Vote(@BsonProperty("customers_id") customerId: Long, @BsonProperty("photos_id") photoId: Long, rating:Long)

trait VoteRepo {

  def getAll: Future[Seq[Vote]]

  def update(vote: Vote): Future[Option[Vote]]

}
