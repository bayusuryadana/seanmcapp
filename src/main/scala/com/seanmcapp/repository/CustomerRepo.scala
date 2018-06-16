package com.seanmcapp.repository

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Customer(id: Long, name: String, isSubscribed: Boolean, hitCount: Long)

class CustomerInfo(tag: Tag) extends Table[Customer](tag, "customers") {
  val id = column[Long]("id", O.PrimaryKey)
  val name = column[String]("name")
  val isSubscribed = column[Boolean]("is_subscribed")
  val hitCount = column[Long]("hit_count")

  def * =
    (id, name, isSubscribed, hitCount) <> (Customer.tupled, Customer.unapply)
}

object CustomerRepo extends TableQuery(new CustomerInfo(_)) with DBComponent {

  def get(id: Long): Future[Option[Customer]] = {
    run(this.filter(_.id === id).result.headOption)
  }

  def getAllSubscribedCust: Future[Seq[Customer]] = {
    run(this.filter(_.isSubscribed).result)
  }

  def update(customer: Customer): Future[Option[Customer]] = {
    run(this.returning(this).insertOrUpdate(customer))
  }

}
