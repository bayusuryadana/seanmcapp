package com.seanmcapp.repository

import org.mongodb.scala.bson.annotations.BsonProperty

import scala.concurrent.Future

case class Vote(@BsonProperty("photos_id") photoId: Long, @BsonProperty("customers_id") customerId: Long, rating:Long)

trait VoteRepo {

  def getAll: Future[Seq[Vote]]

  def update(vote: Vote): Future[Option[Vote]]

}
