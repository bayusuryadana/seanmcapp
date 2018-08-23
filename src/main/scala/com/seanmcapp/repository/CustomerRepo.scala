package com.seanmcapp.repository

import scala.concurrent.Future

case class Customer(id: Long, name: String)

trait CustomerRepo {

  def getAll: Future[Seq[Customer]]

  def update(customer: Customer): Future[Option[Customer]]

}
