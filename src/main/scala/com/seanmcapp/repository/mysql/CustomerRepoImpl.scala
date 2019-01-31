package com.seanmcapp.repository.mysql

import com.seanmcapp.repository.{Customer, CustomerRepo}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class CustomerInfo(tag: Tag) extends Table[Customer](tag, "customers") {
  val id = column[Long]("id", O.PrimaryKey)
  val name = column[String]("name")
  val platform = column[String]("platform")

  def * = (id, name, platform) <> (Customer.tupled, Customer.unapply)
}

class CustomerRepoImpl extends TableQuery(new CustomerInfo(_)) with CustomerRepo with DBComponent {

  def update(customer: Customer): Future[Option[Customer]] = {
    run(this.returning(this).insertOrUpdate(customer))
  }

  // TODO: get rid of this to Join
  def getAll: Future[Seq[Customer]] = {
    run(this.result)
  }

}
