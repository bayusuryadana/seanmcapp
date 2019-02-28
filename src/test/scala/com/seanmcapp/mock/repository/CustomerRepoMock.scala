package com.seanmcapp.mock.repository

import com.seanmcapp.repository.instagram.{Customer, CustomerRepo}

import scala.concurrent.Future

object CustomerRepoMock extends CustomerRepo {

  private val customerList = Seq(
    Customer(-111546505, "Kelompok abang redho", "telegram"),
    Customer(-209240150, "OMOM", "telegram"),
    Customer(123, "PAWAS", "android"),
    Customer(26694991, "Cahaya Ikhwan", "telegram"),
    Customer(274852283, "Bayu บายู", "telegram")
  )

  override def getAll: Future[Seq[Customer]] = Future.successful(customerList)

  override def insertOrUpdate(customer: Customer): Future[Int] = Future.successful(1)
}
