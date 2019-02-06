package com.seanmcapp.repository.instagram

import scala.concurrent.Future

case class Vote(customerId: Long, photoId: Long, rating:Long)

trait VoteRepo {

  def getAll: Future[Seq[Vote]]

  def insertOrUpdate(vote: Vote): Future[Int]

}
