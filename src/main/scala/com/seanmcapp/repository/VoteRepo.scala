package com.seanmcapp.repository

import org.mongodb.scala.bson.annotations.BsonProperty

import scala.concurrent.Future

case class Vote(@BsonProperty("photos_id") photoId: String, @BsonProperty("customers_id") customerId: Long, rating:Long)

trait VoteRepo {

  def update(vote: Vote): Future[Option[Vote]]

}
