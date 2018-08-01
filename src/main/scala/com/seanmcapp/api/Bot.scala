package com.seanmcapp.api

import com.seanmcapp.repository._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait Bot {

  val customerRepo: CustomerRepo
  val voteRepo: VoteRepo
  val photoRepo: PhotoRepo

  def getRandom(user: Option[Customer], callback: Photo => Int): Future[Option[Int]] = {
    user.foreach(resetCustomer)
    photoRepo.getRandom.map(_.map(callback))
  }

  def getRandom(account: String, user: Option[Customer], callback: Photo => Int): Future[Option[Int]] = {
    user.foreach(resetCustomer)
    photoRepo.getRandom(account).map(_.map(callback))
  }

  private def resetCustomer(customerDefault: Customer): Unit = {
    customerRepo.update(customerDefault)
  }

  def vote(vote: Vote): Unit = {
    voteRepo.update(vote)
  }

}
