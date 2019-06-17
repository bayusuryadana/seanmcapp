package com.seanmcapp.mock.repository

import com.seanmcapp.repository.instagram.{Customer, CustomerRepo}

import scala.concurrent.Future

object CustomerRepoMock extends CustomerRepo {

  private val customerList = Seq(
    Customer(1L, "Riandra Ramadhana", 0),
    Customer(2L, "Rizky Harlistyoputro", 128),
    Customer(3L, "Muhammad Redho Ayassa", 64)
  )

  override def get(id: Long): Future[Option[Customer]] = Future.successful(customerList.find(_.id == id))

  override def insert(customer: Customer): Future[Int] = Future.successful(1)

  override def update(customer: Customer): Future[Int] = Future.successful(1)
}
