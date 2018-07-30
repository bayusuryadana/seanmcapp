package com.seanmcapp.repository.postgre

import com.seanmcapp.repository.{Customer, CustomerRepo}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

class CustomerInfo(tag: Tag) extends Table[Customer](tag, "customers") {
  val id = column[Long]("id", O.PrimaryKey)
  val name = column[String]("name")
  val isSubscribed = column[Boolean]("is_subscribed")

  def * =
    (id, name, isSubscribed) <> (Customer.tupled, Customer.unapply)
}

class CustomerRepoImpl extends TableQuery(new CustomerInfo(_)) with CustomerRepo with DBComponent {

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
