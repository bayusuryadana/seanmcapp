package com.seanmcapp.repository.instagram

import com.seanmcapp.repository.DBComponent
import com.seanmcapp.repository.instagram.PhotoRepoImpl.run
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Customer(id: Long, name: String, count: Int)

class CustomerInfo(tag: Tag) extends Table[Customer](tag, "customers") {
  val id = column[Long]("id", O.PrimaryKey)
  val name = column[String]("name")
  val count = column[Int]("count")

  def * = (id, name, count) <> (Customer.tupled, Customer.unapply)
}

trait CustomerRepo {

  def get(id: Long): Future[Option[Customer]]

  def insert(customer: Customer): Future[Int]

  def update(customer: Customer): Future[Int]

}

object CustomerRepoImpl extends TableQuery(new CustomerInfo(_)) with CustomerRepo with DBComponent {

  def get(id: Long): Future[Option[Customer]] = {
    run(this.filter(_.id === id).take(1).result.headOption)
  }

  def insert(customer: Customer): Future[Int] = run(this += customer)

  def update(customer: Customer): Future[Int] = {
    run(this.filter(_.id === customer.id).update(customer))
  }

  // this function is only for testing
  def delete(customer: Customer): Future[Int] = {
    run(this.filter(_.id === customer.id).delete)
  }

}
