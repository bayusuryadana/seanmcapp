package com.seanmcapp.repository

import scala.concurrent.Future

case class Customer(id: Long, name: String)

trait CustomerRepo {

  def get(id: Long): Future[Option[Customer]]

  def getAll: Future[Seq[Customer]]

  def update(customer: Customer): Future[Option[Customer]]

}
