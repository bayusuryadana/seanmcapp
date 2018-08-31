package com.seanmcapp.api

import com.seanmcapp.repository._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait Service {

  val customerRepo: CustomerRepo
  val voteRepo: VoteRepo
  val photoRepo: PhotoRepo

  def getRandom[T](customer: Customer, callback: Photo => T): Future[Option[T]] = {
    customerRepo.update(customer)
    photoRepo.getRandom.map(_.map(callback))
  }

  def getRandom[T](account: String, customer: Customer, callback: Photo => T): Future[Option[T]] = {
    customerRepo.update(customer)
    photoRepo.getRandom(account).map(_.map(callback))
  }

  def doVote(vote: Vote): Future[Option[Vote]] = {
    voteRepo.update(vote)
  }

}
