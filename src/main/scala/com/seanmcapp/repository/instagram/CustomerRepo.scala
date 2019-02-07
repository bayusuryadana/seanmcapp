package com.seanmcapp.repository.instagram

import com.seanmcapp.repository.DBComponent
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Customer(id: Long, name: String, platform: String)

class CustomerInfo(tag: Tag) extends Table[Customer](tag, "customers") {
  val id = column[Long]("id", O.PrimaryKey)
  val name = column[String]("name")
  val platform = column[String]("platform")

  def * = (id, name, platform) <> (Customer.tupled, Customer.unapply)
}

trait CustomerRepo {

  def getAll: Future[Seq[Customer]]

  def insertOrUpdate(customer: Customer): Future[Int]

}

object CustomerRepoImpl extends TableQuery(new CustomerInfo(_)) with CustomerRepo with DBComponent {

  def insertOrUpdate(customer: Customer): Future[Int] = {
    val q = this.filter(_.id === customer.id)
    for {
      length <- run(q.length.result)
      affectedRow <- run(if (length > 0) q.update(customer) else this += customer)
    } yield {
      affectedRow
    }
  }

  // TODO: get rid of this to Grafana
  def getAll: Future[Seq[Customer]] = {
    run(this.result)
  }

}
