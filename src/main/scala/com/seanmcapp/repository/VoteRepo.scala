package com.seanmcapp.repository

import scala.concurrent.Future

case class Vote(id: String, photoId: String, customerId: Long, rating:Long)

trait VoteRepo {

  def update(vote: Vote): Future[Option[Vote]]

}
