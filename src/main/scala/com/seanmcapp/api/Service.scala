package com.seanmcapp.api

import com.seanmcapp.repository._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait Service {

  val customerRepo: CustomerRepo
  val voteRepo: VoteRepo
  val photoRepo: PhotoRepo
  val trackRepo: TrackRepo
  val accountRepo: AccountRepo

  def getRandom[T](customer: Customer, isFromGroup: Option[Customer], account: Option[String])(callback: Photo => T): Future[Option[T]] = {
    photoRepo.getRandom(account).map(_.map { photo =>
      doTracking(customer, photo, isFromGroup).map(res => println("[INFO] Track done: " + res))
      callback(photo)
    })
  }

  def doVote(vote: Vote): Future[Int] = {
    voteRepo.insertOrUpdate(vote)
  }

  private def doTracking[T](customer: Customer, photo: Photo, isFromGroup: Option[Customer]): Future[Seq[Int]] = {
    // update user info
    val (customerF, customerId) = if (isFromGroup.isDefined) {
      val groupCustomer = isFromGroup.get
      (customerRepo.insertOrUpdate(groupCustomer), groupCustomer.id)
    } else {
      (customerRepo.insertOrUpdate(customer), customer.id)
    }

    val track = Track(customerId, photo.id, System.currentTimeMillis / 1000)
    val trackF = trackRepo.insert(track)

    Future.sequence(Seq(customerF, trackF))
  }

}
