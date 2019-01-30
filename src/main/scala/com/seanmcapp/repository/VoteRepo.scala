package com.seanmcapp.repository

import scala.concurrent.Future

case class Vote(customerId: Long, photoId: Long, rating:Long)

trait VoteRepo {

  def getAll: Future[Seq[Vote]]

  def update(vote: Vote): Future[Option[Vote]]

}
