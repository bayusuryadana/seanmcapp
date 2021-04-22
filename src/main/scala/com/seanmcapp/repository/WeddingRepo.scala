package com.seanmcapp.repository

import com.seanmcapp.repository.DBComponent
import com.seanmcapp.repository.instagram.CustomerRepoImpl.run
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Wedding(id: Long, name: String, message: Option[String])

class WeddingInfo(tag: Tag) extends Table[Wedding](tag, "weddings") {
  val id = column[Long]("id", O.PrimaryKey)
  val name = column[String]("name")
  val message = column[Option[String]]("message")
  
  def * = (id, name, message) <> (Wedding.tupled, Wedding.unapply)
}

trait WeddingRepo {
  
  def getAll: Future[Seq[Wedding]]

  def get(id: Long): Future[Option[Wedding]]

  def insert(customer: Wedding): Future[Int]

  def update(customer: Wedding): Future[Int]
  
}

object WeddingRepoImpl extends TableQuery(new WeddingInfo(_)) with WeddingRepo with DBComponent {
  
  override def getAll: Future[Seq[Wedding]] = run(this.result)
  
  override def get(id: Long): Future[Option[Wedding]] = {
    run(this.filter(_.id === id).take(1).result.headOption)
  }

  override def insert(customer: Wedding): Future[Int] = run(this += customer)

  override def update(wedding: Wedding): Future[Int] = {
    run(this.filter(_.id === wedding.id).update(wedding))
  }
  
}
