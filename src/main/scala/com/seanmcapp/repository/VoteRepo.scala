package com.seanmcapp.repository

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Vote(id: String, photoId: String, customerId: Long, rating:Long)

class VoteInfo(tag: Tag) extends Table[Vote](tag, "votes") {
  val id = column[String]("id", O.PrimaryKey)
  val photoId = column[String]("photos_id")
  val customerId = column[Long]("customers_id")
  val rating = column[Long]("rating")

  def * = (id, photoId, customerId, rating) <> (Vote.tupled, Vote.unapply)
}

object VoteRepo extends TableQuery(new VoteInfo(_)) with DBComponent {

  def update(vote: Vote): Future[Option[Vote]] = {
    run(this.returning(this).insertOrUpdate(vote))
  }

}
