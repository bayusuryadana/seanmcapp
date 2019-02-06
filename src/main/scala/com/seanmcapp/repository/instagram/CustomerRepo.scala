package com.seanmcapp.repository.instagram

import scala.concurrent.Future

case class Customer(id: Long, name: String, platform: String)

trait CustomerRepo {

  def getAll: Future[Seq[Customer]]

  def insertOrUpdate(customer: Customer): Future[Int]

}
