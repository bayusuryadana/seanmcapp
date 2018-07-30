package com.seanmcapp.repository

import scala.concurrent.Future

case class Customer(id: Long, name: String, isSubscribed: Boolean)

trait CustomerRepo {

  def get(id: Long): Future[Option[Customer]]

  def getAllSubscribedCust: Future[Seq[Customer]]

  def update(customer: Customer): Future[Option[Customer]]

}
